/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.spell.trick.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.internal.MathHelper;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;
import vazkii.psi.api.spell.wrapper.EntityListWrapper;

import java.util.ArrayList;
import java.util.List;

public class PieceTrickCollapseBlockSequence extends PieceTrick {

	SpellParam<Vector3> position;
	SpellParam<Vector3> target;
	SpellParam<Number> maxBlocks;

	public PieceTrickCollapseBlockSequence(Spell spell) {
		super(spell);
	}

	@Override
	public void initParams() {
		addParam(position = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.BLUE, false, false));
		addParam(target = new ParamVector(SpellParam.GENERIC_NAME_TARGET, SpellParam.GREEN, false, false));
		addParam(maxBlocks = new ParamNumber(SpellParam.GENERIC_NAME_MAX, SpellParam.RED, false, true));
	}

	@Override
	public Class<?> getEvaluationType() {
		return EntityListWrapper.class;
	}

	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);

		Double maxBlocksVal = this.<Double>getParamEvaluation(maxBlocks);
		if (maxBlocksVal == null || maxBlocksVal <= 0) {
			throw new SpellCompilationException(SpellCompilationException.NON_POSITIVE_VALUE, x, y);
		}

		meta.addStat(EnumSpellStat.POTENCY, (int) (maxBlocksVal * 20));
		meta.addStat(EnumSpellStat.COST, (int) ((150 + (maxBlocksVal - 1) * 100)));
	}

	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Vector3 positionVal = this.getParamValue(context, position);
		Vector3 targetVal = this.getParamValue(context, target);
		int maxBlocksInt = this.getParamValue(context, maxBlocks).intValue();

		if (positionVal == null) {
			throw new SpellRuntimeException(SpellRuntimeException.NULL_VECTOR);
		}

		ItemStack tool = context.tool;
		if (tool.isEmpty()) {
			tool = PsiAPI.getPlayerCAD(context.caster);
		}

		World world = context.caster.world;
		Vector3 targetNorm = targetVal.copy().normalize();
		List<Entity> list = new ArrayList<>();
		for (BlockPos blockPos : MathHelper.getBlocksAlongRay(positionVal.toVec3D(), positionVal.copy().add(targetNorm.copy().multiply(maxBlocksInt)).toVec3D(), maxBlocksInt)) {
			if (!context.isInRadius(Vector3.fromBlockPos(blockPos))) {
				throw new SpellRuntimeException(SpellRuntimeException.OUTSIDE_RADIUS);
			}
			BlockState state = world.getBlockState(blockPos);

			if (!world.isBlockModifiable(context.caster, blockPos)) {
				break;
			}

			if (state.getBlockHardness(world, blockPos) != -1 &&
					PieceTrickBreakBlock.canHarvestBlock(state, context.caster, world, blockPos, tool) &&
					world.getTileEntity(blockPos) == null) {

				BlockEvent.BreakEvent event = PieceTrickBreakBlock.createBreakEvent(state, context.caster, world, blockPos, tool);
				MinecraftForge.EVENT_BUS.post(event);
				if (event.isCanceled()) {
					break;
				}

				FallingBlockEntity falling = new FallingBlockEntity(world, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, state);
				world.addEntity(falling);
				list.add(falling);
			}
		}

		return EntityListWrapper.make(list);
	}
}
