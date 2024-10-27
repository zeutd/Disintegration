package disintegration.world.meta;

import mindustry.Vars;
import mindustry.world.meta.BuildVisibility;

public class DTBuildVisibility {
    public static final BuildVisibility
    campaignDisable = new BuildVisibility(() -> Vars.state == null || !Vars.state.isCampaign());
}
