package com.earth.xo;

/**
 * Created by God on 2017/2/9.
 */

public class Waster {
}

/*

Following are from Chatting.java

        /*
        private void uploadImageToQiniu(final IMMessage message, String filePath, final ViewHolderRightImage cell, final int messageType) {
            try {
                JsonMessage msg = new JsonMessage();
                msg.file = Bitmap2StrByBase64(filePath);
                msg.messageType = CommonValue.kWCMessageTypeImage;
                msg.text = "[图片]";
                Gson gson = new Gson();
                String json = gson.toJson(msg);
                message.setContent(json);
                sendMediaMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
//			String bucketName = "dchat";
//	        PutPolicy putPolicy = new PutPolicy(bucketName);
//			Config.ACCESS_KEY = "ZJQmyoJNrIuXq81X-naqxYkMkjlvhNUiVgdQXBk6";
//	        Config.SECRET_KEY = "NrM8Fl1OmxqliLTtoP9m94JkIGf8vIuca7O_OG-9";
//	        Mac mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
//	        String auploadToken = null;
//			try {
//				auploadToken = putPolicy.token(mac);
//				Logger.i(auploadToken);
//			} catch (Exception e) {
//				Logger.i(e);
//			}
//			String key = IO.UNDEFINED_KEY;
//			PutExtra extra = new PutExtra();
//			extra.params = new HashMap<String, String>();
//			IO.putFile(auploadToken, key, new File(filePath), extra, new JSONObjectRet() {
//				@Override
//				public void onProcess(long current, long total) {
//					if (messageType == CommonValue.kWCMessageTypePlain) {
//						float percent = (float) (current*1.0/total)*100;
//						if ((int)percent < 100) {
//							cell.photoProgress.setText((int)percent+"%");
//						}
//						else if ((int)percent == 100) {
//							cell.photoProgress.setText("处理中...");
//						}
//					}
//				}
//
//				@Override
//				public void onSuccess(JSONObject resp) {
//					String key = resp.optString("hash", "");
//					try {
//						JsonMessage msg = new JsonMessage();
//						msg.file = "http://7xj66h.com1.z0.glb.clouddn.com/"+key;
//						Logger.i(msg.file);
//						switch (messageType) {
//						case CommonValue.kWCMessageTypeImage:
//							msg.messageType = CommonValue.kWCMessageTypeImage;
//							msg.text = "[图片]";
//							break;
//
//						case CommonValue.kWCMessageTypeVoice:
//							msg.messageType = CommonValue.kWCMessageTypeVoice;
//							msg.text = "[语音]";
//							break;
//						}
//						Gson gson = new Gson();
//						String json = gson.toJson(msg);
//						message.setContent(json);
//						sendMediaMessage(message);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//
//				@Override
//				public void onFailure(Exception ex) {
//					Logger.i(ex.toString());
//				}
//			});
        }

        private void uploadVoiceToQiniu(final IMMessage message, String filePath, final ViewHolderRightVoice cell, final int messageType) {

            try {
                JsonMessage msg = new JsonMessage();
                msg.file = encodeBase64File(filePath);
                msg.messageType = CommonValue.kWCMessageTypeVoice;
                msg.text = "[语音]";
                Gson gson = new Gson();
                String json = gson.toJson(msg);
                message.setContent(json);
                sendMediaMessage(message);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
//			String bucketName = "dchat";
//	        PutPolicy putPolicy = new PutPolicy(bucketName);
//			Config.ACCESS_KEY = "ZJQmyoJNrIuXq81X-naqxYkMkjlvhNUiVgdQXBk6";
//	        Config.SECRET_KEY = "NrM8Fl1OmxqliLTtoP9m94JkIGf8vIuca7O_OG-9";
//	        Mac mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
//	        String auploadToken = null;
//			try {
//				auploadToken = putPolicy.token(mac);
//				Logger.i(auploadToken);
//			} catch (Exception e) {
//				Logger.i(e);
//			}
//			String key = IO.UNDEFINED_KEY;
//			PutExtra extra = new PutExtra();
//			extra.params = new HashMap<String, String>();
//			IO.putFile(auploadToken, key, new File(filePath), extra, new JSONObjectRet() {
//				@Override
//				public void onProcess(long current, long total) {
//				}
//
//				@Override
//				public void onSuccess(JSONObject resp) {
//					String key = resp.optString("hash", "");
//					try {
//						JsonMessage msg = new JsonMessage();
//						msg.file = "http://7xj66h.com1.z0.glb.clouddn.com/"+key;
//						Logger.i(msg.file);
//						switch (messageType) {
//						case CommonValue.kWCMessageTypeImage:
//							msg.messageType = CommonValue.kWCMessageTypeImage;
//							msg.text = "[图片]";
//							break;
//
//						case CommonValue.kWCMessageTypeVoice:
//							msg.messageType = CommonValue.kWCMessageTypeVoice;
//							msg.text = "[语音]";
//							break;
//						}
//						Gson gson = new Gson();
//						String json = gson.toJson(msg);
//						message.setContent(json);
//						sendMediaMessage(message);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//
//				@Override
//				public void onFailure(Exception ex) {
//					Logger.i(ex.toString());
//				}
//			});
        }
    }*/
