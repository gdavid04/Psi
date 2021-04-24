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

import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrickCollapseBlock extends PieceTrick {

	SpellParam<Vector3> position;

	public PieceTrickCollapseBlock(Spell spell) {
		super(spell);
	}

	@Override
	public void initParams() {
		addParam(position = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.BLUE, false, false));
	}

	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);

		meta.addStat(EnumSpellStat.POTENCY, 80);
		meta.addStat(EnumSpellStat.COST, 125);
	}

	@Override
	public Class<?> getEvaluationType() {
		return Entity.class;
	}

	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		ItemStack tool = context.getHarvestTool();
		Vector3 positionVal = this.getParamValue(context, position);

		if (positionVal == null) {
			throw new SpellRuntimeException(SpellRuntimeException.NULL_VECTOR);
		}
		if (!context.isInRadius(positionVal)) {
			throw new SpellRuntimeException(SpellRuntimeException.OUTSIDE_RADIUS);
		}

		World world = context.caster.getEntityWorld();
		BlockPos pos = positionVal.toBlockPos();
		BlockState state = world.getBlockState(pos);

		if (!world.isBlockModifiable(context.caster, pos)) {
			throw new SpellRuntimeException(SpellRuntimeException.IMMUNE_TARGET);
		}

		if (state.getBlockHardness(world, pos) != -1 &&
				PieceTrickBreakBlock.canHarvestBlock(state, context.caster, world, pos, tool) &&
				world.getTileEntity(pos) == null) {

			BlockEvent.BreakEvent event = PieceTrickBreakBlock.createBreakEvent(state, context.caster, world, pos, tool);
			MinecraftForge.EVENT_BUS.post(event);
			if (event.isCanceled()) {
				throw new SpellRuntimeException(SpellRuntimeException.IMMUNE_TARGET);
			}

			FallingBlockEntity falling = new FallingBlockEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, state);
			world.addEntity(falling);
			return falling;
		}
		throw new SpellRuntimeException(SpellRuntimeException.NULL_TARGET);
	}

}
