package metadev.digital.metacustomitemslib.storage.async;

import metadev.digital.metacustomitemslib.storage.DataStoreException;
import metadev.digital.metacustomitemslib.storage.IDataStore;

public interface IDataStoreTask<T>
{
	public T run(IDataStore store) throws DataStoreException;
	
	public boolean readOnly();
}
