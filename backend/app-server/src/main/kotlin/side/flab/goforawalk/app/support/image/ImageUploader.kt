package side.flab.goforawalk.app.support.image

import org.springframework.web.multipart.MultipartFile

interface ImageUploader {
    /**
     * 업로드된 이미지를 클라우드에 저장하고, 이미지 URL을 반환한다.
     *
     * @param file 업로드할 이미지 파일
     * @param fileName 저장할 파일 이름
     * @return 업로드된 이미지의 URL
     */
    fun uploadImage(file: MultipartFile, fileName: String): String
}