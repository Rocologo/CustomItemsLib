package metadev.digital.metacustomitemslib.storage;

public interface IDataCallback<T>
{
	void onCompleted(T data);
	
	void onError(Throwable error);
}
