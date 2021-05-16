package ninjaphenix.expandedstorage.chest.client;

public final class TopChestModel extends SingleChestModel {
    public TopChestModel() {
        super(64, 48);
        LID.addBox(0, 0, 0, 14, 5, 14, 0);
        LID.addBox(6, -2, 14, 2, 4, 1, 0);
        LID.setPos(1, 9, 1);
        BASE.addBox(0, 0, 0, 14, 10, 14, 0);
        BASE.setPos(1, 0, 1);
    }
}
