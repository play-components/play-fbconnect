package controllers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import play.Play;
import play.exceptions.UnexpectedException;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.modules.fbconnect.FBConnectPlugin;
import play.modules.fbconnect.FBConnectSession;
import play.mvc.*;

public class FBConnect extends Controller {

    public static void callback() {
        FBConnectPlugin fbplugin = Play.plugin(FBConnectPlugin.class);
        FBConnectSession fbsession = fbplugin.session();
        String code = params.get("code");
        if(code != null && !code.isEmpty()){
            String authUrl = fbsession.getAuthUrl(code);
            String response = WS.url(authUrl).get().getString();
            String accessToken = null;
            Integer expires = null;
            String[] pairs = response.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length != 2) {
                    throw new UnexpectedException("Module fbconnect got an unexpected auth response from facebook");
                } else {
                    if (kv[0].equals("access_token")) {
                        accessToken = kv[1];
                    }
                    if (kv[0].equals("expires")) {
                        expires = Integer.valueOf(kv[1]);
                    }
                }
            }
            if (accessToken != null && expires != null) {
                try{
                    Class model = Class.forName(fbsession.getModel());
                    Class[] paramTypes = new Class[1];
                    paramTypes[0] = JsonObject.class;
                    Method method = model.getMethod("facebookOAuthCallback", paramTypes);
                    if(Modifier.isStatic(method.getModifiers())){
                        String uri = "https://graph.facebook.com/me?access_token="+accessToken;
                        JsonObject jsonData = WS.url(uri).get().getJson().getAsJsonObject();
                        method.invoke(null, jsonData);
                    }else{
                        throw new UnexpectedException("Module fbconnect expects your facebookOAuthCallback method to be static");
                    }
                }catch(ClassNotFoundException e){
                    throw new UnexpectedException("Module fbconnect cannot find your model class "+fbsession.getModel());
                }catch(NoSuchMethodException e){
                    throw new UnexpectedException("Module fbconnect requires that your model class "+fbsession.getModel()+" must provide a method with this signature: [public static void facebookOAuthCallback(JsonObject data)]");
                }catch(IllegalAccessException e){
                    throw new UnexpectedException("Module fbconnect does not have access to call your model's findForFacebookOAuth");
                }catch(InvocationTargetException e){
                    throw new UnexpectedException("Module fbconnect could not call your model's findForFacebookOAuth: "+e.getMessage());
                }
            } else {
                throw new UnexpectedException("Module fbconnect could not find access token and expires in facebook callback");
            }
        }
        redirect(fbsession.getLandUrl());
    }
    

}
