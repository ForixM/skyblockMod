package fr.modcraftmc.skyblock.client.gui;

import com.feed_the_beast.mods.ftbguilibrary.icon.Icon;
import com.feed_the_beast.mods.ftbguilibrary.utils.MouseButton;
import com.feed_the_beast.mods.ftbguilibrary.widget.GuiBase;
import com.feed_the_beast.mods.ftbguilibrary.widget.SimpleTextButton;
import com.feed_the_beast.mods.ftbguilibrary.widget.TextField;
import fr.modcraftmc.skyblock.network.PacketHandler;
import fr.modcraftmc.skyblock.network.PacketOpenGUI;
import fr.modcraftmc.skyblock.network.demands.GuiCommand;
import fr.modcraftmc.skyblock.network.demands.Request;
import net.minecraft.util.text.StringTextComponent;

public class GuiError extends GuiBase {

    String errorMessage;
    private TextField error1;
    private TextField error2;
    private TextField error3;
    private TextField error4;
    private TextField error5;
    private TextField error6;
    private SimpleTextButton back;

    public GuiError(String errorMessage){
        this.errorMessage = errorMessage;
        this.setHeight(100);
        this.openGui();
    }

    private String[] factoriseMessage(String message){
        long time1 = System.currentTimeMillis();
        StringBuilder line1 = new StringBuilder(message);
        StringBuilder line2 = new StringBuilder(message);
        StringBuilder line3 = new StringBuilder(message);
        StringBuilder line4 = new StringBuilder(message);
        StringBuilder line5 = new StringBuilder(message);
        StringBuilder line6 = new StringBuilder(message);
        switch ((int)Math.floor((float)message.length()/40)){
            case 0:
                return new String[]{line1.toString(), "", "", "", "", ""};
            case 1:
                line1.delete(40, line1.length());
                line2.delete(0, 40);
                return new String[]{line1.toString(), line2.toString(), "", "", "", ""};
            case 2:
                line1.delete(40, line1.length());
                line2.delete(80, line2.length());
                line2.delete(0, 40);
                line3.delete(0, 80);
                return new String[]{line1.toString(), line2.toString(), line3.toString(), "", "", ""};
            case 3:
                line1.delete(40, line1.length());
                line2.delete(80, line2.length());
                line3.delete(120, line3.length());
                line2.delete(0, 40);
                line3.delete(0, 80);
                line4.delete(0, 120);
                return new String[]{line1.toString(), line2.toString(), line3.toString(), line4.toString(), "", ""};
            case 4:
                line1.delete(40, line1.length());
                line2.delete(80, line2.length());
                line3.delete(120, line3.length());
                line4.delete(160, line4.length());
                line2.delete(0, 40);
                line3.delete(0, 80);
                line4.delete(0, 120);
                line5.delete(0, 160);
                return new String[]{line1.toString(), line2.toString(), line3.toString(), line4.toString(), line5.toString(), ""};
            case 5:
                line1.delete(40, line1.length());
                line2.delete(80, line2.length());
                line3.delete(120, line3.length());
                line4.delete(160, line4.length());
                line5.delete(200, line5.length());
                line2.delete(0, 40);
                line3.delete(0, 80);
                line4.delete(0, 120);
                line5.delete(0, 160);
                line6.delete(0, 200);
                return new String[]{line1.toString(), line2.toString(), line3.toString(), line4.toString(), line5.toString(), line6.toString()};
        }
        long time2 = System.currentTimeMillis();
        System.out.println("time="+(time2-time1));
        return new String[]{"error", "error", "error", "error", "error", "error"};
    }

    @Override
    public void alignWidgets() {
        super.alignWidgets();
        error1.setPosAndSize(10, 10, 100, 10);
        error2.setPosAndSize(10, 20, 100, 10);
        error3.setPosAndSize(10, 30, 100, 10);
        error4.setPosAndSize(10, 40, 100, 10);
        error5.setPosAndSize(10, 50, 100, 10);
        error6.setPosAndSize(10, 60, 100, 10);
        back.setPosAndSize(this.width/2-25, 72, 50, 20);
    }

    @Override
    public void addWidgets() {
        String[] formattedMessage = factoriseMessage(errorMessage);
        error1 = new TextField(this).setText(formattedMessage[0]);
        error2 = new TextField(this).setText(formattedMessage[1]);
        error3 = new TextField(this).setText(formattedMessage[2]);
        error4 = new TextField(this).setText(formattedMessage[3]);
        error5 = new TextField(this).setText(formattedMessage[4]);
        error6 = new TextField(this).setText(formattedMessage[5]);
        back = new SimpleTextButton(this, new StringTextComponent("Back"), Icon.EMPTY) {
            @Override
            public void onClicked(MouseButton mouseButton) {
//                PacketHandler.INSTANCE.sendToServer(new PacketOpenGUI(Request.MAIN, null, GuiCommand.EMPTY));
                closeGui();
            }
        };

        add(error1);
        add(error2);
        add(error3);
        add(error4);
        add(error5);
        add(error6);
        add(back);
    }
}
