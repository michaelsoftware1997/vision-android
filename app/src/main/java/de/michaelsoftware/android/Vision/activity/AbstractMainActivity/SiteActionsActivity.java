package de.michaelsoftware.android.Vision.activity.AbstractMainActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import net.michaelsoftware.android.jui.network.HttpPostJsonHelper;

import java.util.Arrays;
import java.util.HashMap;

import de.michaelsoftware.android.Vision.tools.FormatHelper;
import de.michaelsoftware.android.Vision.tools.ThemeUtils;
import de.michaelsoftware.android.Vision.tools.network.JsonParserAsync;
import de.michaelsoftware.android.Vision.tools.storage.SharedPreferencesHelper;

/**
 * Created by Michael on 12.05.2016.
 */
public abstract class SiteActionsActivity extends LoginActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        juiParserLocal.addAction("changeUser", 0, this);
        juiParserLocal.addAction("clearCache", 0, this);
        juiParserLocal.addAction("activateNotifications", 0, this);
        juiParserLocal.addAction("deactivateNotifications", 0, this);
        juiParserLocal.addAction("activateDeveloper", 0, this);
        juiParserLocal.addAction("deactivateDeveloper", 0, this);
        juiParserLocal.addAction("activateLightMode", 0, this);
        juiParserLocal.addAction("activateDarkMode", 0, this);
        juiParserLocal.addAction("activateExternalImages", 0, this);
        juiParserLocal.addAction("deactivateExternalImages", 0, this);
        juiParserLocal.addAction("activateExternalVideos", 0, this);
        juiParserLocal.addAction("deactivateExternalVideos", 0, this);
        juiParserLocal.addAction("autoHideActionbar", 1, this);
        juiParserLocal.addAction("changeAuthtoken", 1, this);
    }


    public void openHome() {
        String urlStr = this.loginHelper.getServer() + "api/plugins.php";
        String dataString = offlineHelper.getData(urlStr);


        offlineHelper.downloadOfflineData(urlStr);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "bearer " + loginHelper.getAuthtoken());

        HttpPostJsonHelper httpPost = new HttpPostJsonHelper();
        httpPost.setOutput(this, "openHomePage");
        httpPost.setHeaders(headers);
        httpPost.execute(urlStr);

        this.mDrawerLayout.closeDrawers();
    }

    @SuppressWarnings("unused") // used by invoke
    public void openHomePage(HashMap<Object, Object> hashMap) {
        String pString = "{\"data\":[";
        pString += "{\"type\":\"container\", \"background\":\"#66000000\", \"padding\":20, \"marginTop\":15, \"marginBottom\":15, \"value\":[";
        pString += "{\"type\":\"heading\",\"value\":\"Guten Tag " + loginHelper.getUsername() + "\"}";
        pString += ",{\"type\":\"text\", \"align\":\"right\", \"value\":\"" + loginHelper.getServer() + "\"}";
        pString += "],\"click\":\"openPlugin('plg_user')\"}";
        pString += ",{\"type\":\"buttonlist\", \"value\":[";

        SharedPreferencesHelper pref = new SharedPreferencesHelper(this, loginHelper.getUsername() + '@' + FormatHelper.getServerName(loginHelper.getServer()));
        String mainPlugins = pref.read("MAINPLUGINS");
        String[] plugins = mainPlugins.split("\\|");

        for (int i = 0; i < hashMap.size(); i++) {
            if(!hashMap.containsKey(i)) continue;

            Object value = hashMap.get(i);
            if(value instanceof HashMap) {
                Object valueName = ((HashMap) value).get("name");
                Object valueId = ((HashMap) value).get("id");
                Object valueIcon = ((HashMap) value).get("icon");
                Boolean addIcon = false;

                if(valueName instanceof String  && valueId instanceof String) {
                    if(!mainPlugins.equals("") && !Arrays.asList(plugins).contains(valueId)) {
                        continue;
                    }

                    if(((HashMap) value).containsKey("visible") && ((HashMap) value).get("visible") instanceof String) {

                        if(!((HashMap) value).get("visible").equals("no"))
                            addIcon = true;
                    } else {
                        addIcon = true;
                    }
                }

                if(addIcon) {
                    pString += "{\"value\":[\"" + valueIcon + "\",\"" + valueName + "\"], \"click\":\"openPlugin('" + valueId + "','','')\"},";
                }
            }
        }

        pString = FormatHelper.removeLast(pString);
        pString += "]}]";
        pString += ",\"head\":{\"refreshable\":\"FALSE\"}}";

        if(!pString.equals("")) {
            JsonParserAsync json = new JsonParserAsync();
            json.setOutput(this, "getContent");
            json.execute(pString);
        }
    }

    public void openSettings() {
        this.disableRefresh();

        String pString = "{\"data\":";

        pString += "[{\"type\":\"heading\",\"value\":\"Einstellungen\"},";

        pString += "{\"type\":\"container\", \"background\":\"#66000000\", \"padding\":20, \"marginTop\":15, \"marginBottom\":15, \"value\":[";
        pString += "{\"type\":\"heading\", \"value\":\"Allgemein\", \"size\":\"small\"}";
        pString += ",{\"type\":\"text\", \"value\":\"Aktueller Server : " + loginHelper.getServer() + "\"}";
        pString += ",{\"type\":\"text\", \"value\":\"Aktueller Benutzer : " + loginHelper.getUsername() + "\"}";

        SharedPreferencesHelper pref = new SharedPreferencesHelper(this, loginHelper.getUsername() + '@' + FormatHelper.getServerName(loginHelper.getServer()));
        String mac = pref.read("MAC");
        String notification = pref.read("NOTIFICATION", "1");
        String developer = pref.read("DEVELOPER", "0");
        boolean externallyImages = pref.readBoolean("IMAGES_EXTERNALLY");
        boolean externallyVideos = pref.readBoolean("VIDEO_EXTERNALLY");
        boolean autoHideActionbar = pref.readBoolean("AUTO_HIDE_ACTIONBAR");

        pString += ",{\"type\":\"text\", \"value\":\"MAC Adresse des Servers : " + mac + "\"}";
        String wolserver = pref.read("WOLSERVER");
        pString += ",{\"type\":\"text\", \"value\":\"Adresse des WOL-Servers : " + wolserver + "\"}";

        try {
            PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int versionNumber = pinfo.versionCode;
            String versionName = pinfo.versionName;

            pString += ",{\"type\":\"text\", \"value\":\"Version : " + versionName + " (" + versionNumber + ")" + "\"}";

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String host = pref.read("HOST", null);
        String wsport = pref.read("WSPORT", null);

        if (host != null && !host.equals("") && wsport != null && !wsport.equals("")) {
            pString += ",{\"type\":\"text\", \"value\":\"WebSocket-Server : " + host + ":" + wsport + "\"}";
        }

        if (this.mService != null && this.mService.getBinder() != null) {
            pString += ",{\"type\":\"text\", \"value\":\"WebSocket-Service : aktiviert\"}";
        }

        pString += ",{\"type\":\"button\", \"value\":\"Benutzer wechseln\", \"click\":\"changeUser\"}";
        pString += ",{\"type\":\"button\", \"value\":\"Offline Daten neuladen\", \"click\":\"clearCache\"}";

        if(ThemeUtils.getTheme(this, loginHelper.getIdentifier()) == 1) {
            pString += ",{\"type\":\"button\", \"value\":\"Helles Design aktivieren\", \"click\":\"activateLightMode\"}";
        } else {
            pString += ",{\"type\":\"button\", \"value\":\"Dunkles Design aktivieren\", \"click\":\"activateDarkMode\"}";
        }
/* TODO: include
        if(autoHideActionbar) {
            pString += ",{\"type\":\"button\", \"value\":\"ActionBar immer einblenden\", \"click\":\"autoHideActionbar('false')\"}";
        } else {
            pString += ",{\"type\":\"button\", \"value\":\"ActionBar automatisch ausblenden\", \"click\":\"autoHideActionbar('true')\"}";
        }*/

        pString += "]},{\"type\":\"container\", \"background\":\"#66000000\", \"padding\":20, \"marginTop\":15, \"marginBottom\":15, \"value\":[";

        pString += "{\"type\":\"heading\", \"value\":\"Medienwiedergabe\", \"size\":\"small\"}";

        if(externallyImages) {
            pString += ",{\"type\":\"text\", \"value\":\"Aktuell werden Bilder in einer anderen App dargestellt.\"}";
            pString += ",{\"type\":\"button\",\"value\":\"Bilder in Vision darstellen\",\"click\":\"deactivateExternalImages\"}";
        } else {
            pString += ",{\"type\":\"text\", \"value\":\"Aktuell werden Bilder mit dem integrierten Bildbetrachter dargestellt.\"}";
            pString += ",{\"type\":\"button\",\"value\":\"Bilder in externer App darstellen\",\"click\":\"activateExternalImages\"}";
        }

        if(externallyVideos) {
            pString += ",{\"type\":\"text\", \"value\":\"Aktuell werden Videos in einer anderen App wiedergegeben.\"}";
            pString += ",{\"type\":\"button\",\"value\":\"Videos in Vision anschauen\",\"click\":\"deactivateExternalVideos\"}";
        } else {
            pString += ",{\"type\":\"text\", \"value\":\"Aktuell werden Videos mit dem integrierten Videoplayer abgespielt.\"}";
            pString += ",{\"type\":\"button\",\"value\":\"Videos in externer App anschauen\",\"click\":\"activateExternalVideos\"}";
        }

        pString += "]},{\"type\":\"container\", \"background\":\"#66000000\", \"padding\":20, \"marginTop\":15, \"marginBottom\":15, \"value\":[";

        pString += "{\"type\":\"heading\", \"value\":\"Benachrichtigungen\", \"size\":\"small\"}";

        if (!notification.equals("0")) {
            pString += ",{\"type\":\"text\", \"value\":\"Benachrichtigungen : aktiviert\"}";
            pString += ",{\"type\":\"button\", \"value\":\"Benachrichtigungen deaktivieren\", \"click\":\"deactivateNotifications\"}";
        } else {
            pString += ",{\"type\":\"text\", \"value\":\"Benachrichtigungen : deaktiviert\", \"color\":\"#FF0000\"}";
            pString += ",{\"type\":\"button\", \"value\":\"Benachrichtigungen aktivieren\", \"click\":\"activateNotifications\"}";
        }

        pString += ",{\"type\":\"text\", \"value\":\"Hinweis: Eine Änderung an den Benachrichtigungseinstellungen kann einige Zeit in Anspruch nehmen, bis sie angewendet wird. Spätestens nach einem Neustart des Gerätes wird die Änderung allerdings angewendet sein.\"}";

        pString += "]},{\"type\":\"container\", \"background\":\"#66000000\", \"padding\":20, \"marginTop\":15, \"marginBottom\":15, \"value\":[";

        pString += "{\"type\":\"heading\", \"value\":\"Entwickleroptionen\", \"size\":\"small\"}";

        if (developer.equals("1")) {
            pString += ",{\"type\":\"text\", \"value\":\"Entwicklermodus : aktiviert\"}";
            pString += ",{\"type\":\"input\", \"name\":\"authtoken\", \"label\":\"Authtoken: \", \"value\":\"" + loginHelper.getAuthtoken() + "\", \"change\":\"changeAuthtoken('this.value')\"}";
            pString += ",{\"type\":\"text\", \"value\":\"Die Weitergabe des Authtokens kann zu Sicherheitsproblemen führen.\", \"color\":\"#FF0000\"}";

            pString += ",{\"type\":\"button\", \"value\":\"Entwicklermodus deaktivieren\", \"click\":\"deactivateDeveloper\"}";
        } else {
            pString += ",{\"type\":\"text\", \"value\":\"Entwicklermodus : deaktiviert\", \"color\":\"#FF0000\"}";
            pString += ",{\"type\":\"button\", \"value\":\"Entwicklermodus aktivieren\", \"click\":\"activateDeveloper\"}";
        }

        pString += "]}]";
        pString += ",\"head\":{\"refreshable\":\"FALSE\"}}";

        juiParserLocal.parse(pString);

        /*
        if (!pString.equals("")) {
            JsonParserAsync json = new JsonParserAsync();
            json.setOutput(this, "getContent");
            json.execute(pString);
        }*/
    }

    public void openShare(String pParameter) {
        this.disableRefresh();

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "bearer " + loginHelper.getAuthtoken());

        HttpPostJsonHelper httpPost = new HttpPostJsonHelper();
        httpPost.setHeaders(headers);
        httpPost.setOutput(this, "getContent");

        HashMap<String, String> nameValuePair = new HashMap<>();

        if(receivedIntent.hasExtra(Intent.EXTRA_STREAM)) {
            Uri receivedUri = this.receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
            httpPost.addDataName("data");
            nameValuePair.put("data", receivedUri.toString());
        } else if(receivedIntent.hasExtra(Intent.EXTRA_TEXT)) {
            String text = this.receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
            nameValuePair.put("data", text);
        }

        httpPost.setPost(nameValuePair);

        httpPost.execute(loginHelper.getServer() + "ajax.php?plugin=" + pParameter + "&page=receiver&get=view");
    }

    protected void getMimeContent(String dataString) {
        JsonParserAsync jsonAsync = new JsonParserAsync();
        jsonAsync.setOutput(this, "getMimeContent");
        jsonAsync.execute(dataString);
    }

    @SuppressWarnings("unused")
    public void getMimeContent(HashMap hm) {
        String type = this.receivedIntent.getType();
        String upperType = FormatHelper.getUnspecifiedMimeType(type);
        String value = "";
        String valueClick = "";

        if(hm != null)
        for(int i=0; i<hm.size(); i++) {
            if(hm.containsKey(i) && hm.get(i) instanceof HashMap) {
                HashMap plugin = (HashMap) hm.get(i);

                if (plugin.containsKey("mime") && plugin.get("mime") instanceof HashMap) {
                    if (((HashMap) plugin.get("mime")).containsValue(type) || ((HashMap) plugin.get("mime")).containsValue(upperType)) {
                        if (plugin.containsKey("name") && plugin.containsKey("id") && plugin.get("name") instanceof String && plugin.get("id") instanceof String) {
                            value += "\"" + plugin.get("name")  + "\",";
                            valueClick += "\"openPlugin('android','share','" + plugin.get("id")  + "')\",";
                        }
                    }
                }
            }
        }


        value = "[" + FormatHelper.removeLast(value) + "]";
        valueClick = "[" + FormatHelper.removeLast(valueClick) + "]";

        if(value.equals("[]")) {
            value = "[\"Für die Verarbeitung des Datentypes " + type + " ist kein Plugin vorhanden.\"]";
            valueClick = "[]";
        }


        String pString = "{\"data\":[";
        pString += "{\"type\":\"heading\",\"value\":\"Plugin auswählen\"},";
        pString += "{\"type\":\"list\",\"value\":" + value + ",\"click\":" + valueClick + "}";
        pString += "]";
        pString += ",\"head\":{\"refreshable\":\"FALSE\"}}";

        if(!pString.equals("")) {
            JsonParserAsync json = new JsonParserAsync();
            json.setOutput(this, "getContent");
            json.execute(pString);
        }
    }

    @SuppressWarnings("unused") // used by invoke
    public void activateNotifications() {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this, loginHelper.getUsername() + '@' + FormatHelper.getServerName(loginHelper.getServer()) );
        sharedPreferencesHelper.store("NOTIFICATION", "1");

        this.openPlugin("android","settings");
    }

    @SuppressWarnings("unused") // used by invoke
    public void deactivateNotifications() {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this, loginHelper.getUsername() + '@' + FormatHelper.getServerName(loginHelper.getServer()) );
        sharedPreferencesHelper.store("NOTIFICATION", "0");

        this.openPlugin("android","settings");
    }

    @SuppressWarnings("unused") // used by invoke
    public void activateDeveloper() {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this, loginHelper.getUsername() + '@' + FormatHelper.getServerName(loginHelper.getServer()) );
        sharedPreferencesHelper.store("DEVELOPER", "1");

        this.openPlugin("android","settings");
    }

    @SuppressWarnings("unused") // used by invoke
    public void deactivateDeveloper() {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this, loginHelper.getUsername() + '@' + FormatHelper.getServerName(loginHelper.getServer()) );
        sharedPreferencesHelper.store("DEVELOPER", "0");

        this.openPlugin("android","settings");
    }

    @SuppressWarnings("unused") // used by invoke
    public void activateExternalImages() {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this, loginHelper.getUsername() + '@' + FormatHelper.getServerName(loginHelper.getServer()) );
        sharedPreferencesHelper.storeBoolean("IMAGES_EXTERNALLY", true);

        this.openPlugin("android","settings");
    }

    @SuppressWarnings("unused") // used by invoke
    public void deactivateExternalImages() {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this, loginHelper.getUsername() + '@' + FormatHelper.getServerName(loginHelper.getServer()) );
        sharedPreferencesHelper.storeBoolean("IMAGES_EXTERNALLY", false);

        this.openPlugin("android","settings");
    }

    @SuppressWarnings("unused") // used by invoke
    public void activateExternalVideos() {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this, loginHelper.getUsername() + '@' + FormatHelper.getServerName(loginHelper.getServer()) );
        sharedPreferencesHelper.storeBoolean("VIDEO_EXTERNALLY", true);

        this.openPlugin("android","settings");
    }

    @SuppressWarnings("unused") // used by invoke
    public void deactivateExternalVideos() {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this, loginHelper.getUsername() + '@' + FormatHelper.getServerName(loginHelper.getServer()) );
        sharedPreferencesHelper.storeBoolean("VIDEO_EXTERNALLY", false);

        this.openPlugin("android","settings");
    }

    @SuppressWarnings("unused") // used by invoke
    public void activateLightMode() {
        ThemeUtils.changeToTheme(this, ThemeUtils.LIGHT, loginHelper);
    }

    @SuppressWarnings("unused") // used by invoke
    public void activateDarkMode() {
        ThemeUtils.changeToTheme(this, ThemeUtils.DARK, loginHelper);
    }

    @SuppressWarnings("unused") // used by invoke
    public void changeUser() {
        loginHelper.openSelectUserAccount();
    }

    @SuppressWarnings("unused") // used by invoke
    public void clearCache() {
        offlineHelper.clear();
        this.loadMenu();
    }

    /*@SuppressWarnings("unused") // used by invoke
    public void autoHideActionbar(String boolString) { TODO: include
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this, loginHelper.getUsername() + '@' + FormatHelper.getServerName(loginHelper.getServer()) );

        if(boolString.equals("true")) {
            sharedPreferencesHelper.storeBoolean("AUTO_HIDE_ACTIONBAR", true);
            this.AUTO_HIDE_ACTIONBAR = true;
        } else {
            sharedPreferencesHelper.storeBoolean("AUTO_HIDE_ACTIONBAR", false);
            this.AUTO_HIDE_ACTIONBAR = false;

            this.showActionBar();
        }

        this.finish();
        this.startActivity(new Intent(this, this.getClass()));

        this.openPlugin("android","settings");
    }*/

    @SuppressWarnings("unused")
    public void changeAuthtoken(String value) {
        loginHelper.setTemporaryAuthtoken(value);
    }

    public void sendMail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"michaelsoftware1997@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
        i.putExtra(Intent.EXTRA_TEXT   , "");


        startActivity(Intent.createChooser(i, "E-Mail senden"));
    }
}
