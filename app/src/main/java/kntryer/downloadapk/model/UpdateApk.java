package kntryer.downloadapk.model;

/**
 * Created by kntryer on 2017/10/13.
 */

public class UpdateApk {

    private String version;
    private String description;
    private String url;
    private int status;//0:非必需更新 1:必须更新

    public UpdateApk(String version, String description, String url,int status) {
        this.version = version;
        this.description = description;
        this.url = url;
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
