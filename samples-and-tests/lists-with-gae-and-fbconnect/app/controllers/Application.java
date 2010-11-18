package controllers;

import play.mvc.*;
import play.modules.gae.*;

import com.google.appengine.api.users.*;

public class Application extends Controller {

    public static void index() {
        if(session.contains("user")) {
            Lists.index();
        }
        render();
    }
    
    public static void logout() {
        session.remove("user");
        redirect("Application.index");
    }

}