/*package com.earth.data.down;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by Frapo on 2017/4/22.
 * Earth 16:28
 * /

public class Svlet {
    public void In(Object req, Object resp){
        String fileName = new String(req.getParameter("filename"));
        //String chunk = req.getParameter("chunk");
        //System.out.println(chunk);
        System.out.println(fileName);
        resp.setContentType("text/json; charset=utf-8");

        TfileUploadStatus file = statusService.get(fileName);

        try {
            if (file != null) {
                int schunk = file.getChunk();
                deleteFile(uploadPath + schunk + "_" + fileName);
                //long off = schunk * Long.parseLong(chunkSize);
                resp.getWriter().write("{\"off\":" + schunk + "}");

            } else {
                resp.getWriter().write("{\"off\":1}");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private static final long serialVersionUID = 1L;

    private FileUploadStatusServiceI statusService;
    String repositoryPath;
    String uploadPath;

    public void init(ServletConfig config) throws ServletException {
        ServletContext servletContext = config.getServletContext();
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        statusService = (FileUploadStatusServiceI) context.getBean("fileUploadStatusServiceImpl");

        repositoryPath = FileUtils.getTempDirectoryPath();
        System.out.println("临时目录：" + repositoryPath);
        uploadPath = config.getServletContext().getRealPath("datas/uploader");
        System.out.println("目录：" + uploadPath);
        File up = new File(uploadPath);
        if (!up.exists()) {
            up.mkdir();
        }
    }

    @SuppressWarnings("unchecked")
    public void doPost() throws  IOException {
        Integer schunk = null;// 分割块数
        Integer schunks = null;// 总分割数
        String name = null;// 文件名
        BufferedOutputStream outputStream = null;
        if (ServletFileUpload.isMultipartContent(request)) {
            try {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                factory.setSizeThreshold(1024);
                factory.setRepository(new File(repositoryPath));// 设置临时目录
                ServletFileUpload upload = new ServletFileUpload(factory);
                upload.setHeaderEncoding("UTF-8");
                upload.setSizeMax(5 * 1024 * 1024 * 1024);// 设置附近大小
                List<FileItem> items = upload.parseRequest(request);
                // 生成新文件名

                String newFileName = null;
                for (FileItem item : items) {
                    if (!item.isFormField()) {// 如果是文件类型
                        name = newFileName;// 获得文件名
                        if (name != null) {
                            String nFname = newFileName;
                            if (schunk != null) {
                                nFname = schunk + "_" + name;
                            }
                            File savedFile = new File(uploadPath, nFname);
                            item.write(savedFile);
                        }
                    } else {
                        // 判断是否带分割信息
                        if (item.getFieldName().equals("chunk")) {
                            schunk = Integer.parseInt(item.getString());
                            //System.out.println(schunk);
                        }
                        if (item.getFieldName().equals("chunks")) {
                            schunks = Integer.parseInt(item.getString());
                        }

                        if (item.getFieldName().equals("name")) {
                            newFileName = new String(item.getString());

                        }

                    }

                }
                //System.out.println(schunk + "/" + schunks);
                if (schunk != null && schunk == 1) {
                    TfileUploadStatus file = statusService.get(newFileName);
                    if (file != null) {
                        statusService.updateChunk(newFileName, schunk);
                    } else {
                        statusService.add(newFileName, schunk, schunks);
                    }

                } else {
                    TfileUploadStatus file = statusService.get(newFileName);
                    if (file != null) {
                        statusService.updateChunk(newFileName, schunk);
                    }
                }
                if (schunk != null && schunk.intValue() == schunks.intValue()) {
                    outputStream = new BufferedOutputStream(new FileOutputStream(new File(uploadPath, newFileName)));
                    // 遍历文件合并
                    for (int i = 1; i <= schunks; i++) {
                        //System.out.println("文件合并:" + i + "/" + schunks);
                        File tempFile = new File(uploadPath, i + "_" + name);
                        byte[] bytes = FileUtils.readFileToByteArray(tempFile);
                        outputStream.write(bytes);
                        outputStream.flush();
                        tempFile.delete();
                    }
                    outputStream.flush();
                }
                response.getWriter().write("{\"status\":true,\"newName\":\"" + newFileName + "\"}");
            } catch (FileUploadException e) {
                e.printStackTrace();
                response.getWriter().write("{\"status\":false}");
            } catch (Exception e) {
                e.printStackTrace();
                response.getWriter().write("{\"status\":false}");
            } finally {
                try {
                    if (outputStream != null)
                        outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
*/