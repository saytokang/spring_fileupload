package x.y.z;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ImageUploadController {
	
	@Resource(name="imageView") 
	ImageView imageView;
	
	@Autowired ImageService imageService;
	
	@RequestMapping("/upload")
	private String uploadView() {
		return "upload";
	}
	
	@RequestMapping(value="/upload", method=RequestMethod.POST)
	public String submit( @RequestParam("imageFile") MultipartFile file,
			ModelMap model) {
		ImageFile saveFile = imageService.save(file);
		System.out.println(saveFile.getId() + " : " + saveFile.getIsImageFile());
		model.addAttribute("imageFile", saveFile);
		return "uploadComplete";
	}
	
	@RequestMapping("/image/{imageId}")
	private ImageView getImage(@PathVariable String imageId, ModelMap modelMap) {
		ImageFile imageFile = imageService.get(imageId);
		modelMap.put("imageFile", imageFile);
		
		return imageView;
	}
	
	@RequestMapping("/uploads")
	private String uploadsView() {
		return "uploads";
	}
	
	@RequestMapping(value="/uploads", method=RequestMethod.POST)
	public String submits( @RequestParam("imageFile") MultipartFile[] files,
			ModelMap model) {
		ImageFile[] saveFiles = imageService.saveFiles(files);
		model.addAttribute("imageFiles", saveFiles);
		return "uploadsComplete";
	}
	
	@RequestMapping(value = "/files/{fileName}", method = RequestMethod.GET)
	public HttpEntity<byte[]> readFile(
	                 @PathVariable("fileName") String fileName) throws IOException {
		
		String path = ImageFile.UPLOAD_PATH +fileName;
//	    byte[] bytes = Files.readAllBytes(Paths.get(path)); // jdk1.7 이상 
	    byte[] bytes = FileUtils.readFileToByteArray(new File(path)); // jdk1.6 이하 

	    
	    String downloadFileName = "DW_" + fileName;
	    String fileExtension = FilenameUtils.getExtension(path);
	    HttpHeaders header = new HttpHeaders();
	    header.setContentType(new MediaType("application", fileExtension));
	    header.set("Content-Disposition", "attachment; filename=" + downloadFileName.replace(" ", "_"));
	    header.setContentLength(bytes.length);

	    return new HttpEntity<byte[]>(bytes, header);
	}
	
}
