package fr.modcraftmc.skyblock.client.gui.widget;

import com.feed_the_beast.mods.ftbguilibrary.icon.Color4I;
import com.feed_the_beast.mods.ftbguilibrary.icon.Icon;
import com.feed_the_beast.mods.ftbguilibrary.icon.ImageIcon;
import com.feed_the_beast.mods.ftbguilibrary.icon.PartIcon;
import com.feed_the_beast.mods.ftbguilibrary.utils.MouseButton;
import com.feed_the_beast.mods.ftbguilibrary.widget.Panel;
import com.feed_the_beast.mods.ftbguilibrary.widget.SimpleTextButton;
import com.feed_the_beast.mods.ftbguilibrary.widget.Theme;
import com.feed_the_beast.mods.ftbguilibrary.widget.WidgetType;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;

public abstract class ClickableTextButton extends SimpleTextButton {

    private static final ImageIcon TEXTURE_WIDGETS = (ImageIcon)Icon.getIcon("textures/gui/widgets.png");
    private static final Icon BUTTON;
    private static final Icon BUTTON_MOUSE_OVER;
    private static final Icon BUTTON_DISABLED;
    private Color4I textColor;

    public boolean punched = false;
    public boolean enabled = true;

    static {
        BUTTON = new PartIcon(TEXTURE_WIDGETS, 0, 66, 200, 20, 4);
        BUTTON_MOUSE_OVER = new PartIcon(TEXTURE_WIDGETS, 0, 86, 200, 20, 4);
        BUTTON_DISABLED = new PartIcon(TEXTURE_WIDGETS, 0, 46, 200, 20, 4);
    }

    public ClickableTextButton(Panel panel, ITextComponent txt, Icon icon) {
        super(panel, txt, icon);
        textColor = Color4I.WHITE;
    }

    public void setTextColor(Color4I color){
        this.textColor = color;
    }

    @Override
    public void drawBackground(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
        super.drawBackground(matrixStack, theme, x, y, w, h);
        if (!enabled)
            BUTTON_DISABLED.draw(x, y, w, h);
        else
            (punched ? BUTTON_MOUSE_OVER : (getWidgetType() == WidgetType.MOUSE_OVER ? BUTTON_MOUSE_OVER : (getWidgetType() == WidgetType.DISABLED ? BUTTON_DISABLED : BUTTON))).draw(x, y, w, h);
    }

    @Override
    public void draw(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
        this.drawBackground(matrixStack, theme, x, y, w, h);
        int s = h >= 16 ? 16 : 8;
        int off = (h - s) / 2;
        ITextProperties title = this.getTitle();
        int textY = y + (h - theme.getFontHeight() + 1) / 2;
        int sw = theme.getStringWidth((ITextProperties)title);
        int mw = w - (this.hasIcon() ? off + s : 0) - 6;
        if (sw > mw) {
            sw = mw;
            title = theme.trimStringToWidth((ITextProperties)title, mw);
        }

        int textX;
        if (this.renderTitleInCenter()) {
            textX = x + (mw - sw + 6) / 2;
        } else {
            textX = x + 4;
        }

        if (this.hasIcon()) {
            this.drawIcon(matrixStack, theme, x + off, y + off, s, s);
            textX += off + s;
        }

        theme.drawString(matrixStack, title, (float)textX, (float)textY, textColor, 2);
    }

    @Override
    public void onClicked(MouseButton mouseButton) {
        if (!enabled)
            return;
    }
}
