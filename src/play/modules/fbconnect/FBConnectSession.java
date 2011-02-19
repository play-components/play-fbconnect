package play.modules.fbconnect;

import java.net.URLEncoder;

import play.Play;
import play.exceptions.UnexpectedException;
import play.libs.WS;
import play.libs.WS.WSRequest;
import play.mvc.Http;
import play.mvc.Router;

public class FBConnectSession {
    private String id;
    private String apiKey;
    private String secret;
    private String model;
    private String landUrl;
    
    public String getAppId() {
        return id;
    }
    public String getApiKey() {
        return apiKey;
    }
    public String getSecret() {
        return secret;
    }
    public String getModel() {
        return model;
    }
    public String getLandUrl() {
        return landUrl;
    }
    
	public String getLoginUrl() {
		return getLoginUrl("email");
	}

    public String getLoginUrl(String scope){
        String url = String.format("https://www.facebook.com/dialog/oauth?client_id=%s&display=%s&redirect_uri=%s", WS.encode(id), WS.encode("page"), WS.encode(Router.getFullUrl("FBConnect.callback")));
        if(scope != null){
            url += "&scope="+WS.encode(scope);
        }
        return url;
    }
    
    public String getAuthUrl(String authCode){
        return String.format("https://graph.facebook.com/oauth/access_token?client_id=%s&redirect_uri=%s&client_secret=%s&code=%s", WS.encode(id), WS.encode(Router.getFullUrl("FBConnect.callback")), WS.encode(secret), WS.encode(authCode));
    }
    
    public void init(){
        if(!Play.configuration.containsKey("fbconnect.id")) {
            throw new UnexpectedException("Module fbconnect requires that you specify fbconnect.id in your application.conf");
        }
        if(!Play.configuration.containsKey("fbconnect.apiKey")){
            throw new UnexpectedException("Module fbconnect requires that you specify fbconnect.apiKey in your application.conf");
        }
        if(!Play.configuration.containsKey("fbconnect.secret")){
            throw new UnexpectedException("Module fbconnect requires that you specify fbconnect.secret in your application.conf");
        }
        if(!Play.configuration.containsKey("fbconnect.model")){
            throw new UnexpectedException("Module fbconnect requires that you specify fbconnect.model in your application.conf");
        }
        if(!Play.configuration.containsKey("fbconnect.landUrl")){
            Play.configuration.setProperty("fbconnect.landUrl", "/");
        }
        id = Play.configuration.getProperty("fbconnect.id");
        apiKey = Play.configuration.getProperty("fbconnect.apiKey");
        secret = Play.configuration.getProperty("fbconnect.secret");
        model = Play.configuration.getProperty("fbconnect.model");
        landUrl = Play.configuration.getProperty("fbconnect.landUrl");
    }
    
    
}
