package earth.seagate.handler;

import com.earth.views.UserInfo;

/**
 * Created by God on 2017/2/9.
 */
public class FriendManager {

    public static UserInfo get(Long etid) {
        return new UserInfo(etid);
    }
}
