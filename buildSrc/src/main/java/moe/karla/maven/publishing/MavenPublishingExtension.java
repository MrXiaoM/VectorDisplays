package moe.karla.maven.publishing;

public class MavenPublishingExtension {

    ///////////////////////////
    // BASIC
    ///////////////////////////

    public enum PublishingType {
        /**
         * (default) a deployment will go through validation and, if it passes, automatically proceed to publish to Maven Central
         */
        AUTOMATIC,
        /**
         * a deployment will go through validation and require the user to manually publish it via the Portal UI
         */
        USER_MANAGED,
    }

    public PublishingType publishingType = PublishingType.AUTOMATIC;

}
