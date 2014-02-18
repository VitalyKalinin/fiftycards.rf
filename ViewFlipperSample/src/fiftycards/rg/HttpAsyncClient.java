package fiftycards.rg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ProgressDialog;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.apache.http.conn.scheme.SchemeRegistry;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.params.HttpParams;
import org.apache.http.conn.scheme.PlainSocketFactory;

import javax.net.ssl.SSLContext;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.KeyStoreException;
import java.security.KeyManagementException;

import javax.security.cert.CertificateException;

import java.security.KeyStore;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.net.Socket;

import javax.security.cert.X509Certificate;

import org.apache.http.params.HttpProtocolParams;
import org.apache.http.HttpVersion;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.protocol.HTTP;

public class HttpAsyncClient extends AsyncTask<String, Activity, String>
{

	
	public boolean busy=false;
	public boolean gettedFromCache=false;
	public boolean showProgressDialog=true;
	public boolean loadBitmap = false;
	public Bitmap bmResult=null; 
	public String url="";
	ProgressDialog dialog;
	public Context context;
	SharedPreferences prefs;
	String msg;
	
	public HttpAsyncClient(Context ctx, String str) {
		this.context=ctx;
		prefs = ctx.getSharedPreferences("authfile", Context.MODE_PRIVATE );
		this.msg=str;
		if(msg==null) msg="Загрузка. Пожалуйста подождите...";
	}
	
	public String getStringWithoutBeginTime(String str) {
        String regexp = "&beginTime=\\d{10}";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(str);
        if(matcher.find()) 
                return matcher.replaceAll("");
        return "";
	}
	
	public String getPage(String address){
		
		String cacheFile=address;
		if(cacheFile.contains("getEPG"))
			cacheFile=getStringWithoutBeginTime(cacheFile);
		cacheFile=cacheFile.replace("http://",	"").replace(":", "").replace("/", "").replace("?", "").replace(",", "");
		String responseTextFromCache="";
		File cd=context.getCacheDir();
		File[] fs=cd.listFiles();
		boolean hasCache=false;
		String hash="";
		String cachefs="";
		
		//проверяем наличие кэш файла и считываем его если нашли в responseTextFromCache
		Log.d("CACHE", "================================================");
		for(int i = 0;i<fs.length;i++){
			if(fs[i].getName().indexOf(cacheFile+".hash=")>-1){
				cachefs=fs[i].getAbsolutePath();
				
				Log.d("CACHE","find cache file: " + cachefs);
				hasCache=true;
				hash=cachefs.substring(cachefs.indexOf(".hash=")+6);
				try {
					gettedFromCache=true;
					responseTextFromCache=readFileAsString(cachefs);
					Log.d("CACHE", "------------------------------------------------");
					Log.d("CACHE","responseText from cache: " + responseTextFromCache);
					Log.d("CACHE", "------------------------------------------------");
//					tryValid(responseText);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		//если имеется доступ к сети отправляем запрос серверу
		if(isOnline()){		  
			HttpClient client;
		    try {
		        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		        trustStore.load(null, null);

		        SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
		        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		        HttpParams params = new BasicHttpParams();
		        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		     // Set the timeout in milliseconds until a connection is established.
		        int timeoutConnection = 3000;
		        HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);
		        // Set the default socket timeout (SO_TIMEOUT) 
		        // in milliseconds which is the timeout for waiting for data.
		        int timeoutSocket = 3000;
		        HttpConnectionParams.setSoTimeout(params, timeoutSocket);
		        
		        SchemeRegistry registry = new SchemeRegistry();
		        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		        registry.register(new Scheme("https", sf, 443));

		        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

		        client = new DefaultHttpClient(ccm, params);
		    } catch (Exception e) {
		        client = new DefaultHttpClient();
		    }

			HttpGet method;
			Log.v("HTTP","auth="+isAuth());
			if(isAuth()){
				if(address.indexOf("?")>1){ 
					String str=address.replace(" ", "%20")+"&sid="+getSid();
					str+="&hashSum="+md5(str);
					Log.v("HTTP",str);
					method = new HttpGet(str);
				}else {
					String str=address.replace(" ", "%20")+"?sid="+getSid();
					str+="&hashSum="+md5(str);
					Log.v("HTTP",str);
					method = new HttpGet(str);
				}
			} else {
				method = new HttpGet(address.replace(" ", "%20"));
				Log.d("HTTP",address.replace(" ", "%20"));
			}
			
			
			Log.d("CACHE","method: " + method);
			
		    if(hasCache) method.setHeader("If-None-Match","\""+hash+"\"");
			HttpResponse response = null;
		    try {
		    	Log.d("CACHE","try get response");
		    	response = client.execute(method);
		    } catch (ClientProtocolException e) {
		        e.printStackTrace();	        
		    } catch (IOException e) {
		        e.printStackTrace();	        
		    }
		    
		    if(response!=null){
		    	Log.d("CACHE","response: " + response);
			    HttpEntity entity = response.getEntity();
			    //если сервер ответил, что данные не измененились или пустой ответ, но есть кэш -> ничего не делаем
			    if(( response.getStatusLine().getStatusCode()==304 || entity==null ) && (hasCache) && (responseTextFromCache!="") && (responseTextFromCache!=null)){
			    	
			    	Log.d("CACHE","status code: " + response.getStatusLine().getStatusCode());
			    	Log.d("CACHE","entity: " + entity);
			    	Log.d("CACHE","hasCache: " + hasCache);
			    	Log.d("CACHE","responseTextFromCache: " + "not null");
			    	
	//		    	try {
	//					responseText=readFileAsString(cachefs);
	//				} catch (IOException e) {
	//					responseText="";
	//					e.printStackTrace();
	//				}
			    //в противном случае записываем ответ в кэш
			    }else{
				    try {
				    	Log.d("CACHE","try save cache file in cache directory");
				    	responseTextFromCache = EntityUtils.toString(entity,"utf8");
				    	gettedFromCache=false;
				    	for(int i = 0;i<fs.length;i++)
							if(fs[i].getName().indexOf(cacheFile+".hash=")>-1)
								fs[i].delete();
				    	if(response.getHeaders("ETag").length>0){
					    	String etag=response.getHeaders("ETag")[0].getValue().replaceAll("\"", "");
					    	writeCacheFile(responseTextFromCache,cd.getAbsolutePath()+"/"+cacheFile+".hash="+etag);
				    	}
				    } catch (ParseException e) {
				    	e.printStackTrace();
				    } catch (IOException e) {
				    	e.printStackTrace();
				    }
			    }
		    }else Log.d("CACHE","response is null");
//		    client.close();
		}else Log.d("CACHE","OffLine");
	    return responseTextFromCache;
	}
	
	public boolean tryValid(String x){
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
	    	factory.setNamespaceAware(true);
	    	XmlPullParser xpp = factory.newPullParser();
	    	xpp.setInput( new StringReader ( x ) );
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean hasCacheFile(String url){
		String cacheFile=url.replace("http://",	"").replace(":", "").replace("/", "").replace("?", "").replace(",", "");
		if(cacheFile.contains("getEPG"))
			cacheFile=getStringWithoutBeginTime(cacheFile);
		File[] fs=context.getCacheDir().listFiles();
		for(int i = 0;i<fs.length;i++){
			if(fs[i].getName().indexOf(cacheFile+".hash=")>-1)
				return true;}
		return false;
	}

	public String getCacheFile(String url){
		String cacheFile=url.replace("http://",	"").replace(":", "").replace("/", "").replace("?", "").replace(",", "");
		if(cacheFile.contains("getEPG"))
			cacheFile=getStringWithoutBeginTime(cacheFile);
		File[] fs=context.getCacheDir().listFiles();
		for(int i = 0;i<fs.length;i++)
			if(fs[i].getName().indexOf(cacheFile+".hash=")>-1)
				try {
					return readFileAsString(fs[i].getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
				}
		return "";
	}
	
	private void writeCacheFile(String text,String fname)	{       
	   File cacheFile = new File(fname);
	   if (cacheFile.exists()) {
		   cacheFile.delete();
	   }
	   try {
	      BufferedWriter buf = new BufferedWriter(new FileWriter(cacheFile, true)); 
	      buf.append(text);
	      buf.newLine();
	      buf.close();
	   }
	   catch (IOException e) {
	      e.printStackTrace();
	   }
	}
	
	private static String readFileAsString(String filePath) throws java.io.IOException{
		FileInputStream stream = new FileInputStream(new File(filePath));
		try {
			FileChannel fc = stream.getChannel();
		    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		    return Charset.defaultCharset().decode(bb).toString();
		} finally {
		    stream.close();
		}
	}
	
	public boolean isAuth(){		
		if(prefs.getString("pref_auth", "defValue").equalsIgnoreCase("logged-in")) return true;
		else return false;
	}
		
	public String getSid() {
		Log.v("HTTPRec","getSid="+prefs.getString("pref_sid", "defValue"));
		return prefs.getString("pref_sid", "defValue");
	}
	
	public HttpCallback callBack;
	
	public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
	
	protected String doInBackground(String... urls) {
		return getPage(urls[0]);
	}
	
	protected void onPreExecute() {
		busy=true;
		callBack.Prepare();
	}
	protected void onPostExecute(String result) {
		busy=false;
		if((context!=null)&&(showProgressDialog)&&(dialog!=null)){ 
			dialog.dismiss();
		}
		callBack.callBack(result,gettedFromCache);
	}

	public static final String md5(final String s) {
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest
	                .getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i = 0; i < messageDigest.length; i++) {
	            String h = Integer.toHexString(0xFF & messageDigest[i]);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        return hexString.toString();

	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return "";
	}
}
