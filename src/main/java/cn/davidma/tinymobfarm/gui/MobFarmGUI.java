package cn.davidma.tinymobfarm.gui;

import java.util.ArrayList;
import java.util.List;

import cn.davidma.tinymobfarm.block.container.MobFarmContainer;
import cn.davidma.tinymobfarm.reference.Info;
import cn.davidma.tinymobfarm.tileentity.MobFarmTileEntity;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class MobFarmGUI extends GuiContainer{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Info.MOD_ID+":textures/gui/farm_gui.png");
	private InventoryPlayer player;
	private MobFarmTileEntity tileEntity;
	
	public MobFarmGUI(InventoryPlayer player, MobFarmTileEntity tileEntity) {
		super(new MobFarmContainer(player, tileEntity));
		this.player = player;
		this.tileEntity = tileEntity;
		
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String name = this.tileEntity.getDisplayName().getFormattedText();
		this.fontRenderer.drawString(name, xSize/2 - this.fontRenderer.getStringWidth(name)/2, 8, 4210752);
		if (this.tileEntity.working()) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(TEXTURE);
			this.drawTexturedModalRect(48, 60, 176, 5, 80, 5);
			this.drawTexturedModalRect(48, 60, 176, 0, progressScale(80), 5);
		} else {
			String text = this.tileEntity.hasLasso() ? "Disabled by redstone" : "Insert a lasso to activate";
			if (this.tileEntity.hasLasso()) {
				if (this.tileEntity.hasHostileMob() && this.tileEntity.getId() < Info.LOWEST_ID_FOR_HOSTILE_SPAWNING) {
					text = "This mob need higher farm tiers.";
				} else {
					text = "Disabled by redstone.";
				}
			} else {
				text = "Insert a lasso to activate";
			}
			int x = xSize / 2 - this.fontRenderer.getStringWidth(text) / 2;
			int y = 60;
			this.fontRenderer.drawString(text, x, 59, 16733525);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(TEXTURE);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		this.drawTip(mouseX, mouseY);
	}
	
	private void drawTip(int mouseX, int mouseY) {
		int btnX = this.guiLeft + 2, btnY = this.guiTop + 2, width = 8, height = 8;
		boolean active = mouseX > btnX && mouseY > btnY && mouseX <= btnX+width && mouseY <= btnY+height;
		if (active) {
			// Nope, no list.addAll()
			List<String> info = new ArrayList<String>();
			
			String mobName = this.tileEntity.getMobName();
			if (mobName != null && !mobName.isEmpty()) {
				info.add(String.format("Current mob type: %s.", mobName));
				info.add("");
			}
			info.add("Disable with redstone.");
			info.add("");
			info.add("Items are ejected to");
			info.add("adjacent containers.");
			int predictX, maxLen = 0;
			for (String i: info) {
				int textWidth = this.fontRenderer.getStringWidth(i);
				if (textWidth > maxLen) maxLen = textWidth;
			}
			predictX = this.guiLeft - (maxLen + 17);
			int xPos = predictX >= -10 ? predictX : this.guiLeft;
			this.drawHoveringText(info, xPos, this.getGuiTop(), this.fontRenderer);
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
	
	private int progressScale(int pixels) {
		int total = this.tileEntity.getField(1);
		if (total == 0) return 0;
		return this.tileEntity.getField(0) * pixels / total;
	}

}