package ninjaphenix.expandedstorage.chest.client;

public class LeftChestModel extends SingleChestModel {
    public LeftChestModel() {
        super(64, 48);
        LID.addBox(0, 0, 0, 15, 5, 14, 0);
        LID.addBox(14, -2, 14, 1, 4, 1, 0);
        LID.setPos(1, 9, 1);
        BASE.addBox(0, 0, 0, 15, 10, 14, 0);
        BASE.setPos(1, 0, 1);
    }
}
