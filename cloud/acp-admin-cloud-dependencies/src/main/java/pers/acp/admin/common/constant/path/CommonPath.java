package pers.acp.admin.common.constant.path;

import pers.acp.springcloud.common.enums.RestPrefix;

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
public interface CommonPath {

    String innerBasePath = "/inner";

    String openInnerBasePath = RestPrefix.OPEN + innerBasePath;

}
