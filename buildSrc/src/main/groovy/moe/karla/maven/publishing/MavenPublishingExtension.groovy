package moe.karla.maven.publishing

class MavenPublishingExtension {

    ///////////////////////////
    // BASIC
    ///////////////////////////

    static enum PublishingType {
        /**
         * (default) a deployment will go through validation and, if it passes, automatically proceed to publish to Maven Central
         */
        AUTOMATIC,
        /**
         * a deployment will go through validation and require the user to manually publish it via the Portal UI
         */
        USER_MANAGED,
    }

    public PublishingType publishingType = PublishingType.AUTOMATIC

    /**
     * Generate sources jar and stub javadoc jar automatic
     */
    public boolean automaticSourcesAndJavadoc = true

}
