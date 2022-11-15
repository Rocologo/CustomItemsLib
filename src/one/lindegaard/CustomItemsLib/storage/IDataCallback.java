package one.lindegaard.CustomItemsLib.storage;

public interface IDataCallback<T>
{
	void onCompleted(T data);
	
	void onError(Throwable error);
}
