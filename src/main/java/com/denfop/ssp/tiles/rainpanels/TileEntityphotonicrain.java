package com.denfop.ssp.tiles.rainpanels;

import com.denfop.ssp.tiles.TileEntityMoonPanel;
import com.denfop.ssp.tiles.TileEntityRainPanel;
import com.denfop.ssp.tiles.TileEntitySolarPanel;
import com.denfop.ssp.tiles.TileEntitySolarPanelsun;

public class TileEntityphotonicrain extends TileEntityRainPanel
{
    public static SolarConfig settings;
    
    public TileEntityphotonicrain() {
        super(TileEntityphotonicrain.settings);
    }

	
}
