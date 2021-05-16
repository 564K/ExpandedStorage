package ninjaphenix.expandedstorage.chest.client;

public class BackChestModel extends SingleChestModel {
    public BackChestModel() {
        super(48, 48);
        LID.addBox(0, 0, 0, 14, 5, 15, 0);
        LID.setPos(1, 9, 1);
        BASE.texOffs(0, 20);
        BASE.addBox(0, 0, 0, 14, 10, 15, 0);
        BASE.setPos(1, 0, 1);
    }
}
