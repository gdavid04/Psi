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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import vazkii.psi.api.internal.TooltipHelper;
import vazkii.psi.client.gui.GuiProgrammer;

public class GuiButtonHelp extends Button {

	final GuiProgrammer gui;

	public GuiButtonHelp(int x, int y, GuiProgrammer gui) {
		super(x, y, 12, 12, StringTextComponent.EMPTY, button -> {});
		this.gui = gui;
	}

	@Override
	public void renderButton(MatrixStack ms, int mouseX, int mouseY, float pTicks) {
		if (!gui.takingScreenshot) {
			boolean overHelp = mouseX > x && mouseY > y && mouseX < x + 12 && mouseY < y + 12;
			gui.getMinecraft().getTextureManager().bindTexture(GuiProgrammer.texture);
			blit(ms, x, y, gui.xSize + (overHelp ? 12 : 0), 193, 12, 12, 512, 512);
			if (overHelp && !Screen.hasAltDown()) {
				gui.tooltip.add(new TranslationTextComponent("psimisc.programmer_help"));
				String ctrl = I18n.format(Minecraft.IS_RUNNING_ON_MAC ? "psimisc.ctrl_mac" : "psimisc.ctrl_windows");
				TooltipHelper.tooltipIfShift(gui.tooltip, () -> {
					int i = 0;
					while (I18n.hasKey("psi.programmer_reference" + i)) {
						gui.tooltip.add(new TranslationTextComponent("psi.programmer_reference" + i++, ctrl));
					}
				});
			}
		}
	}
}
