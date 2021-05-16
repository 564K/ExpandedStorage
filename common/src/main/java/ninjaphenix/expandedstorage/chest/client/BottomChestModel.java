package ninjaphenix.expandedstorage.chest.client;

public class BottomChestModel extends SingleChestModel {
    public BottomChestModel() {
        super(64, 32);
        BASE.texOffs(0, 0);
        BASE.addBox(0, 0, 0, 14, 16, 14, 0);
        BASE.setPos(1, 0, 1);
    }
}
