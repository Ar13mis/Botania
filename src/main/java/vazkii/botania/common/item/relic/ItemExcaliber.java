/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Mar 29, 2015, 10:12:50 PM (GMT)]
 */
package vazkii.botania.common.item.relic;

/*
public class ItemExcaliber extends ItemManasteelSword implements IRelic, ILensEffect {

	private static final String TAG_ATTACKER_USERNAME = "attackerUsername";
	private static final String TAG_HOME_ID = "homeID";

	public static ToolMaterial toolMaterial = EnumHelper.addToolMaterial("B_EXCALIBER", 3, -1, 6.2F, 6F, 40);

	Achievement achievement;

	public ItemExcaliber() {
		super(toolMaterial, LibItemNames.EXCALIBER);
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
		if(par3Entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) par3Entity;
			ItemRelic.updateRelic(par1ItemStack, player);
			if(ItemRelic.isRightPlayer(player, par1ItemStack)) {
				PotionEffect haste = player.getActivePotionEffect(Potion.digSpeed);
				float check = haste == null ? 0.16666667F : haste.getAmplifier() == 1 ? 0.5F : 0.4F;

				if(player.getCurrentEquippedItem() == par1ItemStack && player.swingProgress == check && !par2World.isRemote) {
					EntityManaBurst burst = getBurst(player, par1ItemStack);
					par2World.spawnEntityInWorld(burst);
					par2World.playSoundAtEntity(player, "botania:terraBlade", 0.4F, 1.4F);
				}
			}
		}
	}

	@Override
	public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List p_77624_3_, boolean p_77624_4_) {
		ItemRelic.addBindInfo(p_77624_3_, p_77624_1_, p_77624_2_);
	}

	@Override
	public void bindToUsername(String playerName, ItemStack stack) {
		ItemRelic.bindToUsernameS(playerName, stack);
	}

	@Override
	public String getSoulbindUsername(ItemStack stack) {
		return ItemRelic.getSoulbindUsernameS(stack);
	}

	@Override
	public Achievement getBindAchievement() {
		return achievement;
	}

	@Override
	public void setBindAchievement(Achievement achievement) {
		this.achievement = achievement;
	}

	@Override
	public boolean usesMana(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isItemTool(ItemStack p_77616_1_) {
		return true;
	}

	@Override
	public int getEntityLifespan(ItemStack itemStack, World world) {
		return Integer.MAX_VALUE;
	}

	@Override
	public Multimap getItemAttributeModifiers() {
		Multimap multimap = HashMultimap.create();
		multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(itemModifierUUID, "Weapon modifier", 10, 0));
		multimap.put(SharedMonsterAttributes.movementSpeed.getAttributeUnlocalizedName(), new AttributeModifier(itemModifierUUID, "Weapon modifier", 0.3, 1));
		return multimap;
	}

	public EntityManaBurst getBurst(EntityPlayer player, ItemStack stack) {
		EntityManaBurst burst = new EntityManaBurst(player);

		float motionModifier = 7F;

		burst.setColor(0xFFFF20);
		burst.setMana(1);
		burst.setStartingMana(1);
		burst.setMinManaLoss(200);
		burst.setManaLossPerTick(1F);
		burst.setGravity(0F);
		burst.setMotion(burst.motionX * motionModifier, burst.motionY * motionModifier, burst.motionZ * motionModifier);

		ItemStack lens = stack.copy();
		ItemNBTHelper.setString(lens, TAG_ATTACKER_USERNAME, player.getName());
		burst.setSourceLens(lens);
		return burst;
	}

	@Override
	public void apply(ItemStack stack, BurstProperties props) {
		// NO-OP
	}

	@Override
	public boolean collideBurst(IManaBurst burst, RayTraceResult pos, boolean isManaBlock, boolean dead, ItemStack stack) {
		return dead;
	}

	@Override
	public void updateBurst(IManaBurst burst, ItemStack stack) {
		EntityThrowable entity = (EntityThrowable) burst;
		AxisAlignedBB axis = new AxisAlignedBB(entity.posX, entity.posY, entity.posZ, entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).expand(1, 1, 1);

		String attacker = ItemNBTHelper.getString(burst.getSourceLens(), TAG_ATTACKER_USERNAME, "");
		int homeID = ItemNBTHelper.getInt(stack, TAG_HOME_ID, -1);
		if(homeID == -1) {
			AxisAlignedBB axis1 = new AxisAlignedBB(entity.posX, entity.posY, entity.posZ, entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).expand(5, 5, 5);
			List<EntityLivingBase> entities = entity.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axis1);
			for(EntityLivingBase living : entities) {
				if(living instanceof EntityPlayer || living instanceof IBossDisplayData || !(living instanceof IMob) || living.hurtTime != 0)
					continue;

				homeID = living.getEntityId();
				ItemNBTHelper.setInt(stack, TAG_HOME_ID, homeID);
				break;
			}
		}

		List<EntityLivingBase> entities = entity.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axis);

		if(homeID != -1) {
			Entity home = entity.worldObj.getEntityByID(homeID);
			if(home != null) {
				Vector3 vecEntity = Vector3.fromEntityCenter(home);
				Vector3 vecThis = Vector3.fromEntityCenter(entity);
				Vector3 vecMotion = vecEntity.sub(vecThis);
				Vector3 vecCurrentMotion = new Vector3(entity.motionX, entity.motionY, entity.motionZ);

				vecMotion.normalize().multiply(vecCurrentMotion.mag());
				burst.setMotion(vecMotion.x, vecMotion.y, vecMotion.z);
			}
		}

		for(EntityLivingBase living : entities) {
			if(living instanceof EntityPlayer && (((EntityPlayer) living).getName().equals(attacker) || MinecraftServer.getServer() != null && !MinecraftServer.getServer().isPVPEnabled()))
				continue;

			if(living.hurtTime == 0) {
				int cost = 1;
				int mana = burst.getMana();
				if(mana >= cost) {
					burst.setMana(mana - cost);
					float damage = 4F + toolMaterial.getDamageVsEntity();
					if(!burst.isFake() && !entity.worldObj.isRemote) {
						EntityPlayer player = living.worldObj.getPlayerEntityByName(attacker);
						living.attackEntityFrom(player == null ? DamageSource.magic : DamageSource.causePlayerDamage(player), damage);
						entity.setDead();
						break;
					}
				}
			}
		}
	}

	@Override
	public boolean doParticles(IManaBurst burst, ItemStack stack) {
		return true;
	}

	@Override
	public EnumRarity getRarity(ItemStack p_77613_1_) {
		return BotaniaAPI.rarityRelic;
	}

}
*/
