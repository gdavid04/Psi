/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.spell.constant;

import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.interval.Interval;
import vazkii.psi.api.interval.IntervalNumber;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellPiece;

public class PieceConstantPi extends SpellPiece {

	public PieceConstantPi(Spell spell) {
		super(spell);
	}

	@Override
	public EnumPieceType getPieceType() {
		return EnumPieceType.CONSTANT;
	}

	@Override
	public Class<?> getEvaluationType() {
		return Double.class;
	}

	@Override
	public @NotNull Interval<?> evaluate() {
		return IntervalNumber.fromValue(Math.PI);
	}

	@Override
	public Object execute(SpellContext context) {
		return Math.PI;
	}

}
