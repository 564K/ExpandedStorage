package ninjaphenix.expandedstorage.chest.client;

public class FrontChestModel extends SingleChestModel {
    public FrontChestModel() {
        super(64, 48);
        LID.addBox(0, 0, 15, 14, 5, 15, 0);
        LID.addBox(6, -2, 30, 2, 4, 1, 0);
        LID.setPos(1, 9, -15);
        BASE.texOffs(0, 20);
        BASE.addBox(0, 0, 0, 14, 10, 15, 0);
        BASE.setPos(1, 0, 0);
    }
}
