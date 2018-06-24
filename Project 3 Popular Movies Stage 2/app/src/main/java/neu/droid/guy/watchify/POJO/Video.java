package neu.droid.guy.watchify.POJO;

import neu.droid.guy.watchify.NetworkingUtils.BuildUrl;

public class Video {
    String key;
    String site;
    String name;
    int size;
    String id;

    public String getKey() {
        return BuildUrl.getYoutubeURL(key);
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
