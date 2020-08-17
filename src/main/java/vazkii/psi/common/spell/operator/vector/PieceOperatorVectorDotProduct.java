/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.spell.operator.vector;

import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorVectorDotProduct extends PieceOperator {

	SpellParam<Vector3> vec1;
	SpellParam<Vector3> vec2;

	public PieceOperatorVectorDotProduct(Spell spell) {
		super(spell);
	}

	@Override
	public void initParams() {
		addParam(vec1 = new ParamVector(SpellParam.GENERIC_NAME_VECTOR1, SpellParam.RED, false, false));
		addParam(vec2 = new ParamVector(SpellParam.GENERIC_NAME_VECTOR2, SpellParam.GREEN, false, false));
	}

	@Override
	public Object execute(SpellContext context) {
		Vector3 v1 = this.getParamValue(context, vec1);
		Vector3 v2 = this.getParamValue(context, vec2);

		return v1.copy().dotProduct(v2);
	}

	@Override
	public Class<?> getEvaluationType() {
		return Double.class;
	}

}
