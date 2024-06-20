package disintegration.core;

import arc.ApplicationListener;
import arc.Core;
import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.*;
import disintegration.DTVars;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static arc.Core.keybinds;

public class DTSaves implements ApplicationListener {
    protected final static byte typeBool = 0, typeInt = 1, typeLong = 2, typeFloat = 3, typeString = 4, typeBinary = 5;
    protected final static int maxBackups = 10;
    protected ExecutorService executor = Threads.executor("Saves Backup", 1);
    protected ObjectMap<String, Object> defaults = new ObjectMap<>();
    protected HashMap<String, Object> values = new HashMap<>();

    public Fi getDataDirectory(){
        return DTVars.DTRoot;
    }
    public Fi getSavesFile(){
        return DTVars.DTRoot.child("save.bin");
    }
    public Fi getBackupSavesFile(){
        return DTVars.DTRoot.child("save_backup.bin");
    }
    public Fi getBackupFolder(){
        return DTVars.DTRoot.child("saves_backups");
    }
    void writeLog(String text){
        try{
            Fi log = getDataDirectory().child("saves.log");
            log.writeString("[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()) + "] " + text + "\n", true);
        }catch(Throwable t){
            Log.err("Failed to write settings log", t);
        }
    }
    @Override
    public void init() {
        try{
            loadValues();
        }catch (Throwable error){
            writeLog("Error in load: " + Strings.getStackTrace(error));
        }
    }

    public synchronized boolean has(String name){
        return values.containsKey(name);
    }

    public synchronized Object get(String name, Object def){
        return values.getOrDefault(name, def);
    }

    public synchronized void put(String name, Object object){
        if(object instanceof Float || object instanceof Integer || object instanceof Boolean || object instanceof Long
                || object instanceof String || object instanceof byte[]){
            values.put(name, object);
        }else{
            throw new IllegalArgumentException("Invalid object stored: " + (object == null ? null : object.getClass()) + ".");
        }
    }

    @Override
    public void exit(){
        try{
            saveValues();
        }catch(Throwable error){
            writeLog("Error in forceSave to " + getSavesFile() + ":\n" + Strings.getStackTrace(error));
        }
    }

    public void loadValues(){
        if(!getSavesFile().exists() && !getBackupSavesFile().exists()){
            writeLog("No saves files found: " + getSavesFile().absolutePath() + " and " + getBackupSavesFile().absolutePath());
            return;
        }

        try{
            loadValues(getSavesFile());
            writeLog("Loaded " + values.size() + " values");

            //back up the save file, as the values have now been loaded successfully
            getSavesFile().copyTo(getBackupSavesFile());
            writeLog("Backed up " + getSavesFile() + " to " + getBackupSavesFile() + " (" + getBackupSavesFile().length() + " bytes)");
        }catch(Throwable e){
            Log.err("Failed to load base settings file, attempting to load backup.", e);
            writeLog("Failed to load base file " + getSavesFile() + ":\n" + Strings.getStackTrace(e));

            Seq<Fi> attempts = getBackupFolder().seq().add(getBackupSavesFile());
            //sort with latest modified file first
            attempts.sort(Structs.comparingLong(f -> -f.lastModified()));

            for(Fi attempt : attempts){
                try{
                    writeLog("Attempting to load backup file: '" + attempt + "'. Length: " + attempt.length());

                    loadValues(attempt);
                    attempt.copyTo(getSavesFile());

                    Log.info("Loaded backup saves file successfully!");
                    writeLog("| Loaded backup saves file after load failure. New saves file length: " + getSavesFile().length());

                    //break out of loop, we're done here
                    return;
                }catch(Throwable e3){
                    writeLog("| Failed to load backup file " + attempts + ":\n" + Strings.getStackTrace(e3));
                    Log.err("Failed to load backup saves file.", e3);
                }
            }
        }
    }

    public synchronized void loadValues(Fi file) throws IOException {
        try(DataInputStream stream = new DataInputStream(file.read(8192))){
            int amount = stream.readInt();
            //current theory: when corruptions happen, the only things written to the stream are a bunch of zeroes
            //try to anticipate this case and throw an exception when 0 values are written
            if(amount <= 0) throw new IOException("0 values are not allowed.");
            for(int i = 0; i < amount; i++){
                String key = stream.readUTF();
                byte type = stream.readByte();

                switch(type){
                    case typeBool:
                        values.put(key, stream.readBoolean());
                        break;
                    case typeInt:
                        values.put(key, stream.readInt());
                        break;
                    case typeLong:
                        values.put(key, stream.readLong());
                        break;
                    case typeFloat:
                        values.put(key, stream.readFloat());
                        break;
                    case typeString:
                        values.put(key, stream.readUTF());
                        break;
                    case typeBinary:
                        int length = stream.readInt();
                        byte[] bytes = new byte[length];
                        stream.read(bytes);
                        values.put(key, bytes);
                        break;
                    default:
                        throw new IOException("Unknown key type: " + type);
                }
            }
            //make sure all data was read - this helps with potential corruption
            int end = stream.read();
            if(end != -1){
                throw new IOException("Trailing settings data; expected EOF, but got: " + end);
            }
        }
    }
    public synchronized void saveValues(){
        Fi file = getSavesFile();

        try(DataOutputStream stream = new DataOutputStream(file.write(false, 8192))){
            stream.writeInt(values.size());

            for(Map.Entry<String, Object> entry : values.entrySet()){
                stream.writeUTF(entry.getKey());

                Object value = entry.getValue();

                if(value instanceof Boolean){
                    stream.writeByte(typeBool);
                    stream.writeBoolean((Boolean)value);
                }else if(value instanceof Integer){
                    stream.writeByte(typeInt);
                    stream.writeInt((Integer)value);
                }else if(value instanceof Long){
                    stream.writeByte(typeLong);
                    stream.writeLong((Long)value);
                }else if(value instanceof Float){
                    stream.writeByte(typeFloat);
                    stream.writeFloat((Float)value);
                }else if(value instanceof String){
                    stream.writeByte(typeString);
                    stream.writeUTF((String)value);
                }else if(value instanceof byte[]){
                    stream.writeByte(typeBinary);
                    stream.writeInt(((byte[])value).length);
                    stream.write((byte[])value);
                }
            }

        }catch(Throwable e){
            //file is now corrupt, delete it
            file.delete();
            throw new RuntimeException("Error writing preferences: " + file, e);
        }

        writeLog("Saving " + values.size() + " values; " + file.length() + " bytes");

        executor.submit(() -> {
            //make sure two backups can't happen at once.
            synchronized(this){
                Fi backupFolder = getBackupFolder();

                Seq<Fi> previous = backupFolder.seq();
                //make sure first file is most recent, last is oldest
                previous.sort(Structs.comparingLong(f -> -f.lastModified()));

                //create new entry in the backup folder
                file.copyTo(backupFolder.child(System.currentTimeMillis() + ".bin"));

                //delete older backups if they exceed the max backup count
                while(previous.size >= maxBackups){
                    previous.pop().delete();
                }
            }
        });
    }
}
