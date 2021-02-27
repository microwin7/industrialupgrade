package com.Denfop.item.reactor;

import com.Denfop.SuperSolarPanels;

import ic2.api.reactor.IReactor;
import ic2.core.Ic2Items;
import net.minecraft.item.ItemStack;

public class ItemReactorproton extends ItemReactorUranium {
  private int time;
private int heat;
private float power;

public ItemReactorproton(String internalName, int cells,int time,int heat,float power) {
    super(internalName, cells, time);
    this.time = time;
    this.heat=heat;
    this.power = power;
    this.setCreativeTab(SuperSolarPanels.tabssp3);
  }
  
  protected int getFinalHeat(IReactor reactor, ItemStack stack, int x, int y, int heat) {
    if (reactor.isFluidCooled()) {
      float breedereffectiveness = reactor.getHeat() / reactor.getMaxHeat();
      if (breedereffectiveness > 0.5D)
        heat *= this.heat; 
    } 
    return heat;
  }
  
  protected ItemStack getDepletedStack(IReactor reactor, ItemStack stack) {
    ItemStack ret;
    switch (this.numberOfCells) {
      case 1:
        ret = SuperSolarPanels.reactorDepletedprotonSimple;
        return new ItemStack(ret.getItem(), 1);
      case 2:
        ret = SuperSolarPanels.reactorDepletedprotonDual;
        return new ItemStack(ret.getItem(), 1);
      case 4:
        ret = SuperSolarPanels.reactorDepletedprotonQuad;
        return new ItemStack(ret.getItem(), 1);
      case 8:
          ret = SuperSolarPanels.reactorDepletedprotoneit;
          return new ItemStack(ret.getItem(), 1);
    } 
    throw new RuntimeException("invalid cell count: " + this.numberOfCells);
  }
  
  public boolean acceptUraniumPulse(IReactor reactor, ItemStack yourStack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatrun) {
    if (!heatrun) {
      float breedereffectiveness = reactor.getHeat() / reactor.getMaxHeat();
      float ReaktorOutput = this.power * breedereffectiveness + 3.0F;
      reactor.addOutput(ReaktorOutput);
    } 
    return true;
  }
}
