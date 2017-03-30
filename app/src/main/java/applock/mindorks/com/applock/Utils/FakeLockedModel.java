package applock.mindorks.com.applock.Utils;

/**
 * Created by farhanbutt19@gmail.com on 3/30/2017.
 */
public class FakeLockedModel {
    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public boolean isFake() {
        return fake;
    }

    public void setFake(boolean fake) {
        this.fake = fake;
    }

    String pkgName;
    boolean fake;

    public FakeLockedModel(String pkgName, boolean fake) {
        this.pkgName = pkgName;
        this.fake = fake;
    }
}
