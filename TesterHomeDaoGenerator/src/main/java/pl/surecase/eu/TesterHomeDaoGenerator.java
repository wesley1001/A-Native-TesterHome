package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class TesterHomeDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1051, "com.testerhome.nativeandroid.dao");
        addFavorite(schema);
        addPraiseHistory(schema);
        new DaoGenerator().generateAll(schema, args[0]);

    }

    private static void addFavorite(Schema schema) {
        Entity favorite = schema.addEntity("Favorite");
        favorite.implementsSerializable();
        favorite.setHasKeepSections(true);
        favorite.addStringProperty("id").primaryKey();
        favorite.addStringProperty("title");
        favorite.addStringProperty("created_at");
        favorite.addStringProperty("nodeName");
        favorite.addIntProperty("userId");
        favorite.addStringProperty("userLogin");
        favorite.addStringProperty("username");
        favorite.addStringProperty("avatarUrl");
    }


    private static void addPraiseHistory(Schema schema){
        Entity userPraiseHistory = schema.addEntity("UserPraiseHistory");
        userPraiseHistory.addStringProperty("topicId").primaryKey();
        userPraiseHistory.addIntProperty("userId");
    }
}
