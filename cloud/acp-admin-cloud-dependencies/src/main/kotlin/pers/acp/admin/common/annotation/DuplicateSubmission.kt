package pers.acp.admin.common.annotation

/**
 * @author zhang by 24/05/2019
 * @since JDK 11
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class DuplicateSubmission(
        /**
         * key的格式，默认"\[key\]"
         *
         * @return key
         */
        val keyExpress: String = defaultKey,
        /**
         * 过期时间，单位毫秒，默认30秒
         *
         * @return 过期时间
         */
        val expire: Long = 30000
) {
    companion object {
        const val defaultKey = "[key]"
    }
}
