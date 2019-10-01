sys = 'fanin';
new_system(sys);
open_system(sys);
% Block 'Block ∅ > S (kf = 10.0)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block1'],'Position',[70.0, 70.0, 310.0, 490.0]);
set_param([sys '/block1'],'KDfw','0.1');
set_param([sys '/block1'],'KDrv','1.0');
set_param([sys '/block1'],'kr','1.0');
set_param([sys '/block1'],'kdeg','1.0');
set_param([sys '/block1'],'ratC','0.0');
set_param([sys '/block1'],'n','1.0');
set_param([sys '/block1'],'A_FB_EN','0');
set_param([sys '/block1'],'B_FB_EN','0');
set_param([sys '/block1'],'sel_rate','1');
set_param([sys '/block1'],'sel_Ctot','1');

% Block 'Block ∅ > T (kf = 10.0)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block2'],'Position',[450.0, 70.0, 690.0, 490.0]);
set_param([sys '/block2'],'KDfw','0.1');
set_param([sys '/block2'],'KDrv','1.0');
set_param([sys '/block2'],'kr','1.0');
set_param([sys '/block2'],'kdeg','1.0');
set_param([sys '/block2'],'ratC','0.0');
set_param([sys '/block2'],'n','1.0');
set_param([sys '/block2'],'A_FB_EN','0');
set_param([sys '/block2'],'B_FB_EN','0');
set_param([sys '/block2'],'sel_rate','1');
set_param([sys '/block2'],'sel_Ctot','1');

% Block 'Block S <> U (kf = 5.0, kr = 2.0)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block3'],'Position',[70.0, 630.0, 310.0, 1050.0]);
set_param([sys '/block3'],'KDfw','0.2');
set_param([sys '/block3'],'KDrv','0.5');
set_param([sys '/block3'],'kr','1.0');
set_param([sys '/block3'],'kdeg','1.0');
set_param([sys '/block3'],'ratC','1.0');
set_param([sys '/block3'],'n','1.0');
set_param([sys '/block3'],'A_FB_EN','1');
set_param([sys '/block3'],'B_FB_EN','0');
set_param([sys '/block3'],'sel_rate','1');
set_param([sys '/block3'],'sel_Ctot','1');

% Block 'Block T <> U (kf = 6.0, kr = 3.0)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block4'],'Position',[450.0, 630.0, 690.0, 1050.0]);
set_param([sys '/block4'],'KDfw','0.16666666666666666');
set_param([sys '/block4'],'KDrv','0.3333333333333333');
set_param([sys '/block4'],'kr','1.0');
set_param([sys '/block4'],'kdeg','1.0');
set_param([sys '/block4'],'ratC','0.0');
set_param([sys '/block4'],'n','1.0');
set_param([sys '/block4'],'A_FB_EN','1');
set_param([sys '/block4'],'B_FB_EN','0');
set_param([sys '/block4'],'sel_rate','0');
set_param([sys '/block4'],'sel_Ctot','0');


% ** Connections **

% Incoming connections for block '∅ > S (kf = 10.0)'
add_block('simulink/Sources/Constant', [sys '/block1_Atot'],'Position',[20.0, 70.0, 40.0, 90.0]);
set_param([sys '/block1_Atot'],'Value','1.0');
set_param([sys '/block1_Atot'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block1_Atot/1','block1/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block1_Btot'],'Position',[20.0, 130.0, 40.0, 150.0]);
set_param([sys '/block1_Btot'],'Value','1.0');
set_param([sys '/block1_Btot'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block1_Btot/1','block1/2','autorouting','on');
add_line(sys, 'block3/7','block1/6','autorouting','on');
add_line(sys, 'block3/16','block1/5','autorouting','on');

% Incoming connections for block '∅ > T (kf = 10.0)'
add_block('simulink/Sources/Constant', [sys '/block2_Atot'],'Position',[400.0, 70.0, 420.0, 90.0]);
set_param([sys '/block2_Atot'],'Value','1.0');
set_param([sys '/block2_Atot'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block2_Atot/1','block2/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block2_Btot'],'Position',[400.0, 130.0, 420.0, 150.0]);
set_param([sys '/block2_Btot'],'Value','1.0');
set_param([sys '/block2_Btot'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block2_Btot/1','block2/2','autorouting','on');
add_line(sys, 'block4/7','block2/6','autorouting','on');
add_line(sys, 'block4/16','block2/5','autorouting','on');

% Incoming connections for block 'S <> U (kf = 5.0, kr = 2.0)'
add_block('simulink/Math Operations/Sum', [sys '/block3_Atot_adder'],'Position',[20.0, 630.0, 40.0, 650.0]);
set_param([sys '/block3_Atot_adder'],'Inputs','++');
add_line(sys, 'block3_Atot_adder/1','block3/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block3_Atot'],'Position',[0.0, 630.0, 20.0, 650.0]);
set_param([sys '/block3_Atot'],'Value','0.0');
set_param([sys '/block3_Atot'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block1/11','block3_Atot_adder/1','autorouting','on');
add_line(sys, 'block3_Atot/1','block3_Atot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block3_Btot'],'Position',[20.0, 690.0, 40.0, 710.0]);
set_param([sys '/block3_Btot'],'Value','1.0');
set_param([sys '/block3_Btot'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block3_Btot/1','block3/2','autorouting','on');
add_line(sys, 'block3/11','block3/3','autorouting','on');
add_line(sys, 'block4/5','block3/6','autorouting','on');
add_line(sys, 'block4/14','block3/5','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block3_Dfree'],'Position',[20.0, 810.0, 40.0, 830.0]);
set_param([sys '/block3_Dfree'],'Value','1.0');
set_param([sys '/block3_Dfree'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block3_Dfree/1','block3/4','autorouting','on');

% Incoming connections for block 'T <> U (kf = 6.0, kr = 3.0)'
add_block('simulink/Math Operations/Sum', [sys '/block4_Atot_adder'],'Position',[400.0, 630.0, 420.0, 650.0]);
set_param([sys '/block4_Atot_adder'],'Inputs','++');
add_line(sys, 'block4_Atot_adder/1','block4/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block4_Atot'],'Position',[380.0, 630.0, 400.0, 650.0]);
set_param([sys '/block4_Atot'],'Value','0.0');
set_param([sys '/block4_Atot'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block2/11','block4_Atot_adder/1','autorouting','on');
add_line(sys, 'block4_Atot/1','block4_Atot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block4_Btot'],'Position',[400.0, 690.0, 420.0, 710.0]);
set_param([sys '/block4_Btot'],'Value','1.0');
set_param([sys '/block4_Btot'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block4_Btot/1','block4/2','autorouting','on');
add_line(sys, 'block3/18','block4/3','autorouting','on');
add_line(sys, 'block3/6','block4/6','autorouting','on');
add_line(sys, 'block3/15','block4/5','autorouting','on');
add_line(sys, 'block3/11','block4/7','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block4_Dfree'],'Position',[400.0, 810.0, 420.0, 830.0]);
set_param([sys '/block4_Dfree'],'Value','1.0');
set_param([sys '/block4_Dfree'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block4_Dfree/1','block4/4','autorouting','on');

%Output

add_block('simulink/Commonly Used Blocks/Scope', [sys '/outputscope'],'Position',[810.0, 50, 840.0, 80]);
set_param([sys '/outputscope'],'NumInputPorts','3');

% Output block '∅ > S (kf = 10.0)'

% Output block '∅ > T (kf = 10.0)'

% Output block 'S <> U (kf = 5.0, kr = 2.0)'
add_line(sys, 'block3/1','outputscope/1','autorouting','on');
add_line(sys, 'block3/11','outputscope/2','autorouting','on');

% Output block 'T <> U (kf = 6.0, kr = 3.0)'
add_line(sys, 'block4/1','outputscope/3','autorouting','on');

% Assign images
img = imread('chip-block.png');
% block1
set_param([sys '/block1'], 'UserDataPersistent', 'on');
ud.img = img;
set_param([sys '/block1'], 'UserData', ud);
Simulink.Mask.create([sys '/block1']);
set_param([sys '/block1'],'MaskDisplay','ud = get_param([''fanin'' ''/ > S (kf = 10.0)''], ''UserData''); image(ud.img);');
set_param([sys '/block1'], 'MaskIconOpaque', 'opaque-with-ports');
% block2
set_param([sys '/block2'], 'UserDataPersistent', 'on');
ud.img = img;
set_param([sys '/block2'], 'UserData', ud);
Simulink.Mask.create([sys '/block2']);
set_param([sys '/block2'],'MaskDisplay','ud = get_param([''fanin'' ''/ > T (kf = 10.0)''], ''UserData''); image(ud.img);');
set_param([sys '/block2'], 'MaskIconOpaque', 'opaque-with-ports');
% block3
set_param([sys '/block3'], 'UserDataPersistent', 'on');
ud.img = img;
set_param([sys '/block3'], 'UserData', ud);
Simulink.Mask.create([sys '/block3']);
set_param([sys '/block3'],'MaskDisplay','ud = get_param([''fanin'' ''/S <> U (kf = 5.0, kr = 2.0)''], ''UserData''); image(ud.img);');
set_param([sys '/block3'], 'MaskIconOpaque', 'opaque-with-ports');
% block4
set_param([sys '/block4'], 'UserDataPersistent', 'on');
ud.img = img;
set_param([sys '/block4'], 'UserData', ud);
Simulink.Mask.create([sys '/block4']);
set_param([sys '/block4'],'MaskDisplay','ud = get_param([''fanin'' ''/T <> U (kf = 6.0, kr = 3.0)''], ''UserData''); image(ud.img);');
set_param([sys '/block4'], 'MaskIconOpaque', 'opaque-with-ports');

% Rename blocks
set_param([sys '/block1'],'Name',' > S (kf = 10.0)');
set_param([sys '/block2'],'Name',' > T (kf = 10.0)');
set_param([sys '/block3'],'Name','S <> U (kf = 5.0, kr = 2.0)');
set_param([sys '/block4'],'Name','T <> U (kf = 6.0, kr = 3.0)');
