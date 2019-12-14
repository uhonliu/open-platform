package com.opencloud.common.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.net.MalformedURLException;

/**
 * 图片工具类
 *
 * @author liuyadu
 */
public final class ImageUtils {
    /**
     * 图片缩放
     *
     * @param org    原图路径
     * @param dest   缩放图路径
     * @param height 高度
     * @param width  宽度
     */
    public static boolean resize(String org, String dest, int height, int width) {
        // 是否进行了压缩
        boolean bol = false;
        String pictype = "";
        if (!"".equals(org) && org != null) {
            pictype = org.substring(org.lastIndexOf(".") + 1, org.length());
        }
        // 缩放比例
        double ratio = 0;
        File o = new File(org);
        File d = new File(dest);
        BufferedImage bi;
        try {
            bi = ImageIO.read(o);
            Image itemp = bi.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
            int itempWidth = bi.getWidth();
            int itempHeight = bi.getHeight();

            // 计算比例
            if ((itempHeight > height) || (itempWidth > width)) {
                ratio = Math.min((new Integer(height)).doubleValue() / itempHeight, (new Integer(width)).doubleValue() / itempWidth);
                AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
                itemp = op.filter(bi, null);
                ImageIO.write((BufferedImage) itemp, pictype, d);
                bol = true;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return bol;
    }

    public static void resizeWidth(String org, String dest, int height, int width) {
        String pictype = "";
        if (!"".equals(org) && org != null) {
            pictype = org.substring(org.lastIndexOf(".") + 1, org.length());
        }
        double ratio = 0; // 缩放比例
        File o = new File(org);
        File d = new File(dest);
        BufferedImage bi;
        try {
            bi = ImageIO.read(o);
            Image itemp = bi.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
            int itempWidth = bi.getWidth();

            // 计算比例
            if (itempWidth != width) {
                ratio = ((new Integer(width)).doubleValue() / itempWidth);
                AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
                itemp = op.filter(bi, null);
                ImageIO.write((BufferedImage) itemp, pictype, d);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 复制文件
     *
     * @param sourcePath
     * @param targetPath
     */
    public static void copyFile(String sourcePath, String targetPath) {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            File sourceFile = new File(sourcePath);
            File targetFile = new File(targetPath);
            if (!targetFile.exists()) {
                targetFile.getParentFile().mkdirs();
            }
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } catch (IOException e) {
        } finally {
            // 关闭流
            try {
                if (inBuff != null) {
                    inBuff.close();
                }
                if (outBuff != null) {
                    outBuff.close();
                }
            } catch (IOException e) {

            }
        }
    }

    /**
     * @return
     */
    public static BufferedImage getBufferedImage(InputStream is) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(is);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return img;
    }

    /**
     * 裁剪
     *
     * @param is
     * @param dirImageFile
     * @param x
     * @param y
     * @param destWidth
     * @param destHeight
     */
    public static void cutImage(InputStream is, File dirImageFile, int x, int y, int destWidth, int destHeight) {
        try {
            Image img;
            BufferedImage bi = getBufferedImage(is);
            ImageFilter cropFilter;
            // 读取源图像
            // 源图宽度
            int srcWidth = bi.getWidth();
            // 源图高度
            int srcHeight = bi.getHeight();
            if (srcWidth >= destWidth && srcHeight >= destHeight) {
                Image image = bi.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
                // 改进的想法:是否可用多线程加快切割速度
                // 四个参数分别为图像起点坐标和宽高
                // 即: CropImageFilter(int x,int y,int width,int height)
                cropFilter = new CropImageFilter(x, y, destWidth, destHeight);
                img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), cropFilter));
                BufferedImage tag = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
                Graphics g = tag.getGraphics();
                // 绘制缩小后的图
                g.drawImage(img, 0, 0, null);
                g.dispose();
                // 输出为文件
                ImageIO.write(tag, "jpg", dirImageFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将图片转换为BASE64加密字符串.
     *
     * @param imagePath 图片路径.
     * @param format    图片格式.
     * @return
     */
    public static String convertImageToByte(String imagePath, String format) {
        File file = new File(imagePath);
        BufferedImage bi = null;
        ByteArrayOutputStream baos = null;
        String result = null;
        try {
            bi = ImageIO.read(file);
            baos = new ByteArrayOutputStream();
            ImageIO.write(bi, format == null ? "jpg" : format, baos);
            byte[] bytes = baos.toByteArray();
            result = EncodeUtils.encodeBase64(bytes).trim();
        } catch (IOException e) {
        } finally {
            bi = null;
            try {
                if (baos != null) {
                    baos.close();
                    baos = null;
                }
            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     * 将图片流转换为BASE64加密字符串.
     *
     * @param imageInputStream
     * @param format           图片格式.
     * @return
     */
    public static String convertImageStreamToByte(InputStream imageInputStream, String format) {
        BufferedImage bi = null;
        ByteArrayOutputStream baos = null;
        String result = null;
        try {
            bi = ImageIO.read(imageInputStream);
            baos = new ByteArrayOutputStream();
            ImageIO.write(bi, format == null ? "jpg" : format, baos);
            byte[] bytes = baos.toByteArray();
            result = EncodeUtils.encodeBase64(bytes).trim();
        } catch (IOException e) {
        } finally {
            try {
                bi = null;
                if (baos != null) {
                    baos.close();
                    baos = null;
                }
            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     * 将BASE64加密字符串转换为图片.
     *
     * @param base64String
     * @param imagePath    图片生成路径.
     * @param format       图片格式.
     */
    public static void convertByteToImage(String base64String, String imagePath, String format) throws Exception {
        byte[] bytes = null;
        ByteArrayInputStream bais = null;
        BufferedImage bi = null;
        File file = null;
        try {
            bytes = EncodeUtils.decodeBase64(base64String);
            bais = new ByteArrayInputStream(bytes);
            bi = ImageIO.read(bais);
            file = new File(imagePath);
            ImageIO.write(bi, format == null ? "jpg" : format, file);
        } finally {
            try {
                bi = null;
                if (bais != null) {
                    bais.close();
                    bais = null;
                }
            } catch (Exception e) {
            }
        }
    }
}
