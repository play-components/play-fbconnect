package play.modules.fbconnect;

import javax.naming.ConfigurationException;

import play.Play;
import play.PlayPlugin;
import play.exceptions.UnexpectedException;
import play.mvc.Router;

public class FBConnectPlugin extends PlayPlugin {
    
    private static ThreadLocal<FBConnectSession> _session = new ThreadLocal<FBConnectSession>();
    
    public FBConnectSession session(){
        FBConnectSession session = _session.get();
        if(session == null){
            session = new FBConnectSession();
            session.init();
            _session.set(session);
        }
        return session;
    }
    
    @Override
    public void onApplicationStart() {
        session();
    }
    
    @Override
    public void onRoutesLoaded() {
        Router.addRoute("GET", "/fbconnect/oauth/callback", "FBConnect.callback");
    }
    
}
