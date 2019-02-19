package cn.davidma.tinymobfarm.common.tileentity;

import javax.annotation.Nullable;

import cn.davidma.tinymobfarm.common.TinyMobFarm;
import cn.davidma.tinymobfarm.core.EnumMobFarm;
import cn.davidma.tinymobfarm.core.Reference;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityMobFarm extends TileEntity implements ITickable {
	
	private ItemStackHandler inventory = new ItemStackHandler(1);
	private EnumMobFarm mobFarmData;
	private int currProgress;
	
	public TileEntityMobFarm(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}
	
	public TileEntityMobFarm() {
		super(TinyMobFarm.tileEntityMobFarm);
	}
	
	public boolean isWorking() {
		if (this.mobFarmData == null || this.getLasso().isEmpty()) return false;
		return this.mobFarmData.isLassoValid(this.getLasso());
	}

	@Override
	public void tick() {
		currProgress++;
		if (!this.world.isRemote() && this.mobFarmData != null) {
			if (currProgress >= this.mobFarmData.getMaxProgress()) {
				System.out.println("Loot");
				this.sendUpdate();
			}
		}
	}
	
	public ItemStack getLasso() {
		return this.inventory.getStackInSlot(0);
	}
	
	public void setMobFarmData(EnumMobFarm mobFarmData) {
		this.mobFarmData = mobFarmData;
	}
	
	@Deprecated
	public ItemStackHandler getInventory() {
		return this.inventory;
	}
	
	public String getUnlocalizedName() {
		if (this.mobFarmData == null) return "block." + Reference.MOD_ID + ".default_mob_farm";
		return this.mobFarmData.getUnlocalizedName();
	}
	
	public void sendUpdate() {
		IBlockState state = this.world.getBlockState(this.pos);
		this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
		this.world.notifyBlockUpdate(pos, state, state, 3);
		this.markDirty();
	}
	
	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.getPos(), 0, this.getUpdateTag());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		this.read(packet.getNbtCompound());
	}
	
	@Override
	public NBTTagCompound getUpdateTag() {
		return this.write(new NBTTagCompound());
	}
	
	@Override
	public void handleUpdateTag(NBTTagCompound nbt) {
		this.read(nbt);
	}
}