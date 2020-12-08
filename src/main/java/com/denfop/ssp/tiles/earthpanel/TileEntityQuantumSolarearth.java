package com.denfop.ssp.tiles.earthpanel;

import com.denfop.ssp.tiles.TileEntityEarthPanel;
import com.denfop.ssp.tiles.TileEntityMoonPanel;
import com.denfop.ssp.tiles.TileEntitySolarPanel;
import com.denfop.ssp.tiles.TileEntitySolarPanelsun;

public class TileEntityQuantumSolarearth extends TileEntityEarthPanel
{
    public static SolarConfig settings;
    
    public TileEntityQuantumSolarearth() {
        super(TileEntityQuantumSolarearth.settings);
    }
}
