package metadev.digital.metacustomitemslib.storage.async;

import metadev.digital.metacustomitemslib.Core;
import metadev.digital.metacustomitemslib.PlayerSettings;
import metadev.digital.metacustomitemslib.storage.DataStoreException;
import metadev.digital.metacustomitemslib.storage.IDataStore;
import metadev.digital.metacustomitemslib.storage.UserNotFoundException;

import org.bukkit.OfflinePlayer;

import java.util.HashSet;

public class PlayerSettingsRetrieverTask implements IDataStoreTask<PlayerSettings> {

	private OfflinePlayer mPlayer;
	private HashSet<Object> mWaiting;

	public PlayerSettingsRetrieverTask(OfflinePlayer player, HashSet<Object> waiting) {
		mPlayer = player;
		mWaiting = waiting;
	}

	public PlayerSettings run(IDataStore store) throws DataStoreException {
		synchronized (mWaiting) {
			try {
				return store.loadPlayerSettings(mPlayer);
			} catch (UserNotFoundException e) {
				Core.getMessages().debug("Creating new PlayerSettings for %s in the CustomItemsLib database.",
						mPlayer.getName());
				String worldgroup = mPlayer.isOnline()
						? Core.getWorldGroupManager().getCurrentWorldGroup(mPlayer)
						: Core.getWorldGroupManager().getDefaultWorldgroup();
				PlayerSettings ps = new PlayerSettings(mPlayer, worldgroup,
						Core.getConfigManager().learningMode, false, null, null,
						System.currentTimeMillis(), System.currentTimeMillis());
				Core.getPlayerSettingsManager().setPlayerSettings(ps);
				return ps;
			} catch (DataStoreException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	@Override
	public boolean readOnly() {
		return true;
	}
}
