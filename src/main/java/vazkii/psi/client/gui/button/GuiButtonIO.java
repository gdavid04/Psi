/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.client.gui.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import vazkii.psi.client.gui.GuiProgrammer;

public class GuiButtonIO extends Button {

	public final boolean out;
	final GuiProgrammer gui;

	public GuiButtonIO(int x, int y, boolean out, GuiProgrammer gui) {
		super(x, y, 12, 12, StringTextComponent.EMPTY, button -> {});
		this.out = out;
		this.gui = gui;
	}

	public GuiButtonIO(int x, int y, boolean out, GuiProgrammer gui, IPressable pressable) {
		super(x, y, 12, 12, StringTextComponent.EMPTY, pressable);
		this.out = out;
		this.gui = gui;
	}

	@Override
	public void renderButton(MatrixStack ms, int par2, int par3, float pticks) {
		if (active && !gui.takingScreenshot) {
			boolean hover = par2 >= x && par3 >= y && par2 < x + width && par3 < y + height;

			Minecraft.getInstance().textureManager.bindTexture(GuiProgrammer.texture);
			RenderSystem.color4f(1F, 1F, 1F, 1F);
			blit(ms, x, y, hover ? 186 + 72 : 174 + 72, out ? 169 + 72 : 181 + 72, width, height, 512, 512);

			if (hover) {
				String key = out ? "psimisc.export_to_clipboard" : "psimisc.import_from_clipboard";
				TextFormatting color = out ? TextFormatting.RED : TextFormatting.BLUE;
				ITextComponent tip = new TranslationTextComponent(key).mergeStyle(color);
				gui.tooltip.add(tip);
				gui.tooltip.add(new TranslationTextComponent("psimisc.must_hold_shift").mergeStyle(TextFormatting.GRAY));
			}
		}
	}

}
