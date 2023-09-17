//package com.dev_marinov.chatalyze.presentation.ui.chat_screen
//
//
//
//val contentChatAndBottomControl = ConstraintSet {
//    val contentChat = createRefFor("contentChat")
//
//    val rowControl = createRefFor("rowControl")
//    val iconAddPhotoVideo = createRefFor("iconAddPhotoVideo")
//    val writeMessage = createRefFor("writeMessage")
//    val iconMakePhoto = createRefFor("iconMakePhoto")
//    val iconSendMessage = createRefFor("iconSendMessage")
//
//    constrain(contentChat) {
//        top.linkTo(parent.top)
//        start.linkTo(parent.start)
//        end.linkTo(parent.start)
//        bottom.linkTo(writeMessage.top)
//        width = Dimension.wrapContent
//        height = Dimension.fillToConstraints
//    }
//
//    constrain(rowControl) {
//        top.linkTo(contentChat.bottom)
//        start.linkTo(parent.start)
//        end.linkTo(parent.end)
//        bottom.linkTo(parent.bottom)
//        width = Dimension.wrapContent
//        height = Dimension.wrapContent
//    }
//
//    constrain(writeMessage) {
//        top.linkTo(parent.top)
//        start.linkTo(writeMessage.end)
//        end.linkTo(iconMakePhoto.start)
//        bottom.linkTo(parent.bottom)
//        width = Dimension.fillToConstraints
//        height = Dimension.wrapContent
//    }
//
//    constrain(iconMakePhoto) {
//        top.linkTo(parent.top)
//        start.linkTo(writeMessage.end)
//        end.linkTo(iconSendMessage.start)
//        bottom.linkTo(parent.bottom)
//        width = Dimension.wrapContent
//        height = Dimension.wrapContent
//    }
//
//    constrain(iconSendMessage) {
//        top.linkTo(parent.top)
//        start.linkTo(iconMakePhoto.end)
//        end.linkTo(parent.end)
//        bottom.linkTo(parent.bottom)
//        width = Dimension.wrapContent
//        height = Dimension.wrapContent
//    }
//}
//
//
//ConstraintLayout(
//constraintSet = contentChatAndBottomControl,
//modifier = Modifier
////    .fillMaxWidth()
//.fillMaxSize()
//.background(colorResource(id = R.color.main_yellow_new_chat_screen))
////.background(Color.Green)
//.padding(top = 16.dp, bottom = 16.dp)
//.layoutId("contentChat")
//) {
//
//    Column(modifier = Modifier.fillMaxSize()) {
//
//
//        Column(
//            modifier = Modifier
//                //   .fillMaxSize()
//                .background(colorResource(id = R.color.main_yellow_new_chat_screen))
//                .padding(top = 16.dp, bottom = 16.dp)
//
//        ) {
//
//            // there will lazy list
//        }
//
//        Row(
//            modifier = Modifier
//                //  .height(200.dp)
//                .background(colorResource(id = R.color.main_violet_light))
//                .layoutId("writeMessage")
//        ) {
//            Icon(
//                modifier = Modifier
//                    .padding(end = 4.dp)
//                    .size(30.dp)
//                    .layoutId("iconAddPhotoVideo"),
//                painter = painterResource(id = R.drawable.ic_add_photo_video),
//                contentDescription = "",
////                tint = colorResource(id = R.color.main_violet),
//                tint = Color.White,
//            )
//
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    //  .width(200.dp)
//                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
//                    .layoutId("writeMessage"),
//                contentAlignment = Alignment.Center
//            ) {
//                TextFieldHintWriteMessage(
//                    value = textMessage,
//                    onValueChanged = { textMessage = it },
//                    // hintText = stringResource(id = R.string.email),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(50.dp)
//                        .clip(RoundedCornerShape(20))
//                        .background(MaterialTheme.colors.surface)
//                        .padding(start = 8.dp, end = 16.dp)
//                    //icon = Icons.Rounded.Mail
//                )
//            }
//
//            Icon(
//                modifier = Modifier
//                    .padding(end = 4.dp)
//                    .size(30.dp)
//                    .layoutId("iconMakePhoto"),
////                        .layoutId("icon_person"),
//                painter = painterResource(id = R.drawable.ic_make_photo),
//                contentDescription = "",
////                tint = colorResource(id = R.color.main_violet),
//                tint = Color.White,
//            )
//
//            Icon(
//                modifier = Modifier
//                    .padding(end = 4.dp)
//                    .size(30.dp)
//                    .layoutId("iconSendMessage"),
////                        .layoutId("icon_person"),
//                painter = painterResource(id = R.drawable.ic_send_message),
//                contentDescription = "",
////                tint = colorResource(id = R.color.main_violet),
//                tint = Color.White,
//            )
//        }
//    }
//}
//}
//}