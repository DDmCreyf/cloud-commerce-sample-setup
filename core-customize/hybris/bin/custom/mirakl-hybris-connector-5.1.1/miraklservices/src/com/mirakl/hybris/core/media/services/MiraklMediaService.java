package com.mirakl.hybris.core.media.services;

import de.hybris.platform.core.model.media.MediaModel;

public interface MiraklMediaService {

    /**
     * Creates a media with the requested id and downloads its content from the {@code downloadUrl}.
     * If a media with this {@code mediaId} already exists then it is updated.
     *
     * @param mediaId id of the media
     * @param downloadUrl url of the media to download
     * @return the downloaded media if the {@code mediaId} does not exist already.
     */
    MediaModel downloadMedia(String mediaId, String downloadUrl);

    /**
     * Creates a media with the requested id and downloads its content from the {@code downloadUrl}.
     *
     * @param mediaId id of the media
     * @param downloadUrl url of the media to download
     * @param forceDownload force to download the media even if a media with this {@code mediaId} already exists
     * @return the downloaded media if the {@code mediaId} does not exist already.
     */
    MediaModel downloadMedia(String mediaId, String downloadUrl, boolean forceDownload);
}
