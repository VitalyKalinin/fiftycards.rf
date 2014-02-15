package shenhengbin.practice;

public interface HttpCallback {
	void Prepare();
	void callBack(String result,boolean gettedFromCache);
}
