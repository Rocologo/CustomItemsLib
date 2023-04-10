package one.lindegaard.CustomItemsLib.storage.async;

import java.util.LinkedHashSet;
import java.util.Set;

import one.lindegaard.CustomItemsLib.Core;
import one.lindegaard.CustomItemsLib.PlayerSettings;
import one.lindegaard.CustomItemsLib.storage.DataStoreException;
import one.lindegaard.CustomItemsLib.storage.IDataStore;

public class StoreTask implements IDataStoreTask<Void> {
	private LinkedHashSet<PlayerSettings> mWaitingPlayerSettings = new LinkedHashSet<PlayerSettings>();

	public StoreTask(Set<Object> waiting) {
		synchronized (waiting) {
			mWaitingPlayerSettings.clear();

			for (Object obj : waiting) {
				if (obj instanceof PlayerSettings)
					mWaitingPlayerSettings.add((PlayerSettings) obj);
			}

			waiting.clear();
		}
	}

	@Override
	public Void run(IDataStore store) throws DataStoreException {
		if (!mWaitingPlayerSettings.isEmpty())
			store.savePlayerSettings(mWaitingPlayerSettings, true);

		Core.getMessages().debug("Saving CustomItemsLib data");
		
		Core.getRewardBlockManager().saveData();
		Core.getWorldGroupManager().save();

		return null;
	}

	@Override
	public boolean readOnly() {
		return false;
	}
}
