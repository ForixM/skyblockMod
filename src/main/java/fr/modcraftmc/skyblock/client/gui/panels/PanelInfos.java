package fr.modcraftmc.skyblock.client.gui.panels;

import com.feed_the_beast.mods.ftbguilibrary.icon.Icon;
import com.feed_the_beast.mods.ftbguilibrary.utils.MouseButton;
import com.feed_the_beast.mods.ftbguilibrary.widget.Panel;
import com.feed_the_beast.mods.ftbguilibrary.widget.SimpleTextButton;
import com.feed_the_beast.mods.ftbguilibrary.widget.TextBox;
import com.feed_the_beast.mods.ftbguilibrary.widget.TextField;
import fr.modcraftmc.skyblock.client.gui.GuiSettings;
import fr.modcraftmc.skyblock.network.PacketHandler;
import fr.modcraftmc.skyblock.network.PacketModifyIslandInfo;
import fr.modcraftmc.skyblock.network.PacketModifyPermission;
import fr.modcraftmc.skyblock.network.demands.GuiCommand;
import net.minecraft.util.text.StringTextComponent;

import static fr.modcraftmc.skyblock.util.Constants.DISPLAY_NAME;

public class PanelInfos extends Panel {

    private TextField islandNameField, islandDescriptionField;
    private TextBox islandNameBox, islandDescriptionBox;
    private String islandName, islandDescription;
    private SimpleTextButton confirm;

    public PanelInfos(Panel panel, String islandName, String islandDescription) {
        super(panel);
        this.islandName = islandName;
        this.islandDescription = islandDescription;
        confirm = new SimpleTextButton(this, new StringTextComponent("Confirm"), Icon.EMPTY) {
            @Override
            public void onClicked(MouseButton mouseButton) {
                PanelInfos.this.islandName = PanelInfos.this.islandNameBox.getText();
                PanelInfos.this.islandDescription = PanelInfos.this.islandDescriptionBox.getText();
                PacketHandler.INSTANCE.sendToServer(new PacketModifyIslandInfo(PanelInfos.this.islandName, PanelInfos.this.islandDescription, ((GuiSettings)PanelInfos.this.parent).getIslandInfos().getOwner()));

//                if (((GuiSettings)parent).getIslandInfos().getOwner().equalsIgnoreCase(DISPLAY_NAME)) {
//                    PacketHandler.INSTANCE.sendToServer(new PacketModifyIslandInfo(PanelInfos.this.islandName, PanelInfos.this.islandDescription, null, GuiCommand.EMPTY));
//                } else {
//                    PacketHandler.INSTANCE.sendToServer(new PacketModifyIslandInfo(PanelInfos.this.islandName, PanelInfos.this.islandDescription, ((GuiSettings)parent).getIslandInfos().getOwner(), GuiCommand.NOTOWNER));
//                }
            }
        };
    }

    @Override
    public void addWidgets() {
        islandNameField = new TextField(this).setText("Island Name");
        islandNameField.setPos(10, 5);

        islandDescriptionField = new TextField(this).setText("Island Description");
        islandDescriptionField.setPos(10, 40);

        islandNameBox = new TextBox(this);
        islandNameBox.setText(islandName);
        islandNameBox.setPosAndSize(10, 15, 140, 20);

        islandDescriptionBox = new TextBox(this);
        islandDescriptionBox.setText(islandDescription);
        islandDescriptionBox.setPosAndSize(10, 50, 140, 20);

//        confirm = new SimpleTextButton(this, new StringTextComponent("Confirm"), Icon.EMPTY) {
//            @Override
//            public void onClicked(MouseButton mouseButton) {
////                PacketHandler.INSTANCE.sendToServer(new PacketModifyIslandInfo(islandNameBox.getText(), islandDescriptionBox.getText()));
//            }
//        };
        confirm.setPosAndSize(10, 100, 140, 20);

        add(islandNameField);
        add(islandDescriptionField);
        add(islandNameBox);
        add(islandDescriptionBox);
        add(confirm);
    }

    @Override
    public void alignWidgets() {

    }
}
