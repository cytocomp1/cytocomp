sys = 'bottsmorales_inh_irrev';
new_system(sys);
open_system(sys);
% Block 'Block E + S <> ES (kf = 10.0, kr = 100.0)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block1'],'Position',[70.0, 70.0, 310.0, 490.0]);
set_param([sys '/block1'],'KDfw','0.1');
set_param([sys '/block1'],'KDrv','0.01');
set_param([sys '/block1'],'kr','1.0');
set_param([sys '/block1'],'kdeg','1.0');
set_param([sys '/block1'],'ratC','0.0');
set_param([sys '/block1'],'n','1.0');
set_param([sys '/block1'],'A_FB_EN','1');
set_param([sys '/block1'],'B_FB_EN','1');
set_param([sys '/block1'],'sel_rate','1');
set_param([sys '/block1'],'sel_Ctot','1');

% Block 'Block E + I <> EI (kf = 10.0, kr = 50.0)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block2'],'Position',[450.0, 70.0, 690.0, 490.0]);
set_param([sys '/block2'],'KDfw','0.1');
set_param([sys '/block2'],'KDrv','0.02');
set_param([sys '/block2'],'kr','1.0');
set_param([sys '/block2'],'kdeg','1.0');
set_param([sys '/block2'],'ratC','0.0');
set_param([sys '/block2'],'n','1.0');
set_param([sys '/block2'],'A_FB_EN','0');
set_param([sys '/block2'],'B_FB_EN','1');
set_param([sys '/block2'],'sel_rate','1');
set_param([sys '/block2'],'sel_Ctot','1');

% Block 'Block ES > E + P (kf = 2.0) (for E)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block3'],'Position',[70.0, 630.0, 310.0, 1050.0]);
set_param([sys '/block3'],'KDfw','0.5');
set_param([sys '/block3'],'KDrv','1.0');
set_param([sys '/block3'],'kr','1.0');
set_param([sys '/block3'],'kdeg','1.0');
set_param([sys '/block3'],'ratC','0.0');
set_param([sys '/block3'],'n','1.0');
set_param([sys '/block3'],'A_FB_EN','1');
set_param([sys '/block3'],'B_FB_EN','0');
set_param([sys '/block3'],'sel_rate','1');
set_param([sys '/block3'],'sel_Ctot','1');

% Block 'Block ES > E + P (kf = 2.0) (for P)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block4'],'Position',[450.0, 630.0, 690.0, 1050.0]);
set_param([sys '/block4'],'KDfw','0.5');
set_param([sys '/block4'],'KDrv','1.0');
set_param([sys '/block4'],'kr','1.0');
set_param([sys '/block4'],'kdeg','1.0');
set_param([sys '/block4'],'ratC','0.0');
set_param([sys '/block4'],'n','1.0');
set_param([sys '/block4'],'A_FB_EN','0');
set_param([sys '/block4'],'B_FB_EN','0');
set_param([sys '/block4'],'sel_rate','1');
set_param([sys '/block4'],'sel_Ctot','1');


% ** Connections **

% Incoming connections for block 'E + S <> ES (kf = 10.0, kr = 100.0)'
add_block('simulink/Math Operations/Sum', [sys '/block1_Atot_adder'],'Position',[20.0, 70.0, 40.0, 90.0]);
set_param([sys '/block1_Atot_adder'],'Inputs','+++');
add_line(sys, 'block1_Atot_adder/1','block1/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block1_Atot'],'Position',[0.0, 70.0, 20.0, 90.0]);
set_param([sys '/block1_Atot'],'Value','1.0');
set_param([sys '/block1_Atot'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block2/9','block1_Atot_adder/1','autorouting','on');
add_line(sys, 'block3/11','block1_Atot_adder/2','autorouting','on');
add_line(sys, 'block1_Atot/1','block1_Atot_adder/3','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block1_Btot'],'Position',[20.0, 130.0, 40.0, 150.0]);
set_param([sys '/block1_Btot'],'Value','10.0');
set_param([sys '/block1_Btot'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block1_Btot/1','block1/2','autorouting','on');
add_line(sys, 'block3/1','block1/3','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block1_Dfree'],'Position',[20.0, 250.0, 40.0, 270.0]);
set_param([sys '/block1_Dfree'],'Value','1.0');
set_param([sys '/block1_Dfree'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block1_Dfree/1','block1/4','autorouting','on');

% Incoming connections for block 'E + I <> EI (kf = 10.0, kr = 50.0)'
add_line(sys, 'block1/1','block2/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block2_Btot'],'Position',[400.0, 130.0, 420.0, 150.0]);
set_param([sys '/block2_Btot'],'Value','5.0');
set_param([sys '/block2_Btot'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block2_Btot/1','block2/2','autorouting','on');
add_line(sys, 'block2/11','block2/3','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block2_Dfree'],'Position',[400.0, 250.0, 420.0, 270.0]);
set_param([sys '/block2_Dfree'],'Value','1.0');
set_param([sys '/block2_Dfree'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block2_Dfree/1','block2/4','autorouting','on');

% Incoming connections for block 'ES > E + P (kf = 2.0) (for E)'
add_line(sys, 'block1/11','block3/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block3_Btot'],'Position',[20.0, 690.0, 40.0, 710.0]);
set_param([sys '/block3_Btot'],'Value','1.0');
set_param([sys '/block3_Btot'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block3_Btot/1','block3/2','autorouting','on');

% Incoming connections for block 'ES > E + P (kf = 2.0) (for P)'
add_block('simulink/Sources/Constant', [sys '/block4_Btot'],'Position',[400.0, 690.0, 420.0, 710.0]);
set_param([sys '/block4_Btot'],'Value','1.0');
set_param([sys '/block4_Btot'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block4_Btot/1','block4/2','autorouting','on');
add_line(sys, 'block4/11','block4/3','autorouting','on');
add_line(sys, 'block3/5','block4/6','autorouting','on');
add_line(sys, 'block3/14','block4/5','autorouting','on');

%Output

add_block('simulink/Commonly Used Blocks/Scope', [sys '/outputscope'],'Position',[810.0, 50, 840.0, 80]);
set_param([sys '/outputscope'],'NumInputPorts','6');

% Output block 'E + S <> ES (kf = 10.0, kr = 100.0)'
add_line(sys, 'block1/1','outputscope/1','autorouting','on');
add_line(sys, 'block1/3','outputscope/2','autorouting','on');

% Output block 'E + I <> EI (kf = 10.0, kr = 50.0)'
add_line(sys, 'block2/3','outputscope/3','autorouting','on');
add_line(sys, 'block2/11','outputscope/4','autorouting','on');

% Output block 'ES > E + P (kf = 2.0) (for E)'
add_line(sys, 'block3/1','outputscope/5','autorouting','on');

% Output block 'ES > E + P (kf = 2.0) (for P)'
add_line(sys, 'block4/11','outputscope/6','autorouting','on');

% Assign images
img = imread('chip-block.png');
% block1
set_param([sys '/block1'], 'UserDataPersistent', 'on');
ud.img = img;
set_param([sys '/block1'], 'UserData', ud);
Simulink.Mask.create([sys '/block1']);
set_param([sys '/block1'],'MaskDisplay','ud = get_param([''bottsmorales_inh_irrev'' ''/block1''], ''UserData''); image(ud.img);');
set_param([sys '/block1'], 'MaskIconOpaque', 'opaque-with-ports');
% block2
set_param([sys '/block2'], 'UserDataPersistent', 'on');
ud.img = img;
set_param([sys '/block2'], 'UserData', ud);
Simulink.Mask.create([sys '/block2']);
set_param([sys '/block2'],'MaskDisplay','ud = get_param([''bottsmorales_inh_irrev'' ''/block2''], ''UserData''); image(ud.img);');
set_param([sys '/block2'], 'MaskIconOpaque', 'opaque-with-ports');
% block3
set_param([sys '/block3'], 'UserDataPersistent', 'on');
ud.img = img;
set_param([sys '/block3'], 'UserData', ud);
Simulink.Mask.create([sys '/block3']);
set_param([sys '/block3'],'MaskDisplay','ud = get_param([''bottsmorales_inh_irrev'' ''/block3''], ''UserData''); image(ud.img);');
set_param([sys '/block3'], 'MaskIconOpaque', 'opaque-with-ports');
% block4
set_param([sys '/block4'], 'UserDataPersistent', 'on');
ud.img = img;
set_param([sys '/block4'], 'UserData', ud);
Simulink.Mask.create([sys '/block4']);
set_param([sys '/block4'],'MaskDisplay','ud = get_param([''bottsmorales_inh_irrev'' ''/block4''], ''UserData''); image(ud.img);');
set_param([sys '/block4'], 'MaskIconOpaque', 'opaque-with-ports');
