/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.spell.trick.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellHelpers;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.StatLabel;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;
import vazkii.psi.common.entity.EntityTrickMote;

public class PieceTrickTill extends PieceTrick {
	SpellParam<Vector3> position;

	public PieceTrickTill(Spell spell) {
		super(spell);
		setStatLabel(EnumSpellStat.POTENCY, new StatLabel(10));
		setStatLabel(EnumSpellStat.COST, new StatLabel(10));
	}

	public static InteractionResult tillBlock(Player player, Level world, BlockPos pos) {
		if(!world.hasChunkAt(pos) || !world.mayInteract(player, pos)) {
			return InteractionResult.PASS;
		}
		BlockHitResult hit = new BlockHitResult(Vec3.ZERO, Direction.UP, pos, false);
		ItemStack save = player.getItemInHand(InteractionHand.MAIN_HAND);
		player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_HOE));
		UseOnContext fakeContext = new UseOnContext(player, InteractionHand.MAIN_HAND, hit);
		player.setItemInHand(InteractionHand.MAIN_HAND, save);
		return Items.IRON_HOE.useOn(fakeContext);
	}

	@Override
	public void initParams() {
		addParam(position = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.BLUE, false, false));
	}

	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		meta.addStat(EnumSpellStat.COST, 10);
		meta.addStat(EnumSpellStat.POTENCY, 10);
	}

	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		BlockPos pos = SpellHelpers.getBlockPos(this, context, position, true, false);
		
		EntityTrickMote.create(context, pos, () -> {
			tillBlock(context.caster, context.focalPoint.level, pos);
		});
		return null;
	}

}
