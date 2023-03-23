/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.spell;

import com.mojang.datafixers.util.Either;
import vazkii.psi.api.spell.*;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SpellCompiler implements ISpellCompiler {

	@Override
	public Either<CompiledSpell, SpellCompilationException> compile(Spell in) {
		try {
			return Either.left(doCompile(in));
		} catch (SpellCompilationException e) {
			return Either.right(e);
		}
	}

	public CompiledSpell doCompile(Spell spell) throws SpellCompilationException {
		if (spell == null) {
			throw new SpellCompilationException(SpellCompilationException.NO_SPELL);
		}

		CompiledSpell compiled = new CompiledSpell(spell);
		
		HashSet<SpellPiece> built = new HashSet<>();

		List<SpellPiece> tricks = compiled.sourceSpell.grid.findPieces(EnumPieceType::isTrick);
		if (tricks.isEmpty()) {
			throw new SpellCompilationException(SpellCompilationException.NO_TRICKS);
		}
		for (SpellPiece trick : tricks) {
			buildPiece(trick, compiled, built);
		}

		if (compiled.metadata.getStat(EnumSpellStat.COST) < 0 || compiled.metadata.getStat(EnumSpellStat.POTENCY) < 0) {
			throw new SpellCompilationException(SpellCompilationException.STAT_OVERFLOW);
		}

		if (spell.name == null || spell.name.isEmpty()) {
			throw new SpellCompilationException(SpellCompilationException.NO_NAME);
		}
		return compiled;
	}

	public void buildPiece(SpellPiece piece, CompiledSpell compiled, Set<SpellPiece> built) throws SpellCompilationException {
		buildPiece(piece, compiled, built, new HashSet<>());
	}

	public void buildPiece(SpellPiece piece, CompiledSpell compiled, Set<SpellPiece> built, Set<SpellPiece> visited) throws SpellCompilationException {
		if (!visited.add(piece)) {
			throw new SpellCompilationException(SpellCompilationException.INFINITE_LOOP, piece.x, piece.y);
		}
		
		if (built.add(piece)) {
			EnumSet<SpellParam.Side> usedSides = EnumSet.noneOf(SpellParam.Side.class);
			for (SpellParam<?> param : piece.paramSides.keySet()) {
				if (checkSideDisabled(param, piece, usedSides)) {
					continue;
				}
				
				SpellParam.Side side = piece.paramSides.get(param);
				
				SpellPiece pieceAt = compiled.sourceSpell.grid.getPieceAtSideWithRedirections(piece.x, piece.y, side,
					redirect -> buildRedirect(redirect, compiled, built));
				
				if (pieceAt == null) {
					throw new SpellCompilationException(SpellCompilationException.NULL_PARAM, piece.x, piece.y);
				}
				if (!param.canAccept(pieceAt)) {
					throw new SpellCompilationException(SpellCompilationException.INVALID_PARAM, piece.x, piece.y);
				}
				
				buildPiece(pieceAt, compiled, built, visited);
			}
			compiled.actions.add(0, compiled.new Action(piece));
			piece.addToMetadata(compiled.metadata);
		}
		visited.remove(piece);
	}

	public void buildRedirect(SpellPiece piece, CompiledSpell compiled, Set<SpellPiece> built) throws SpellCompilationException {
		if (built.add(piece)) {
			EnumSet<SpellParam.Side> usedSides = EnumSet.noneOf(SpellParam.Side.class);
			
			for (SpellParam<?> param : piece.paramSides.keySet()) {
				checkSideDisabled(param, piece, usedSides);
			}
			
			piece.addToMetadata(compiled.metadata);
		}
	}

	/** @return whether this piece should get skipped over */
	private boolean checkSideDisabled(SpellParam<?> param, SpellPiece parent, EnumSet<SpellParam.Side> seen) throws SpellCompilationException {
		SpellParam.Side side = parent.paramSides.get(param);
		if (side.isEnabled()) {
			if (!seen.add(side)) {
				throw new SpellCompilationException(SpellCompilationException.SAME_SIDE_PARAMS, parent.x, parent.y);
			}
			return false;
		} else {
			if (!param.canDisable) {
				throw new SpellCompilationException(SpellCompilationException.UNSET_PARAM, parent.x, parent.y);
			}
			return true;
		}
	}

}
