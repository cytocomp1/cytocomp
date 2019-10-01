sys = 'catalysis';
new_system(sys);
open_system(sys);
% Block 'Block E + S <> ES (kf = 10.0, kr = 5.0)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block1'],'Position',[70.0, 70.0, 310.0, 490.0]);
set_param([sys '/block1'],'KDfw','0.1');
set_param([sys '/block1'],'KDrv','0.2');
set_param([sys '/block1'],'kr','1.0');
set_param([sys '/block1'],'kdeg','1.0');
set_param([sys '/block1'],'ratC','0.0');
set_param([sys '/block1'],'n','1.0');
set_param([sys '/block1'],'A_FB_EN','1');
set_param([sys '/block1'],'B_FB_EN','1');
set_param([sys '/block1'],'sel_rate','1');
set_param([sys '/block1'],'sel_Ctot','1');

% Block 'Block ES > P + E (kf = 1000.0)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block2'],'Position',[450.0, 70.0, 690.0, 490.0]);
set_param([sys '/block2'],'KDfw','0.001');
set_param([sys '/block2'],'KDrv','1.0');
set_param([sys '/block2'],'kr','1.0');
set_param([sys '/block2'],'kdeg','1.0');
set_param([sys '/block2'],'ratC','0.0');
set_param([sys '/block2'],'n','1.0');
set_param([sys '/block2'],'A_FB_EN','1');
set_param([sys '/block2'],'B_FB_EN','0');
set_param([sys '/block2'],'sel_rate','1');
set_param([sys '/block2'],'sel_Ctot','1');


% ** Connections **

% Incoming connections for block 'E + S <> ES (kf = 10.0, kr = 5.0)'
add_block('simulink/Math Operations/Sum', [sys '/block1_Atot_adder'],'Position',[20.0, 70.0, 40.0, 90.0]);
set_param([sys '/block1_Atot_adder'],'Inputs','++');
add_line(sys, 'block1_Atot_adder/1','block1/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block1_Atot'],'Position',[0.0, 70.0, 20.0, 90.0]);
set_param([sys '/block1_Atot'],'Value','1.0');
set_param([sys '/block1_Atot'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block2/11','block1_Atot_adder/1','autorouting','on');
add_line(sys, 'block1_Atot/1','block1_Atot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block1_Btot'],'Position',[20.0, 130.0, 40.0, 150.0]);
set_param([sys '/block1_Btot'],'Value','10.0');
set_param([sys '/block1_Btot'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block1_Btot/1','block1/2','autorouting','on');
add_line(sys, 'block2/1','block1/3','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block1_Dfree'],'Position',[20.0, 250.0, 40.0, 270.0]);
set_param([sys '/block1_Dfree'],'Value','1.0');
set_param([sys '/block1_Dfree'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block1_Dfree/1','block1/4','autorouting','on');

% Incoming connections for block 'ES > P + E (kf = 1000.0)'
add_block('simulink/Math Operations/Sum', [sys '/block2_Atot_adder'],'Position',[400.0, 70.0, 420.0, 90.0]);
set_param([sys '/block2_Atot_adder'],'Inputs','++');
add_line(sys, 'block2_Atot_adder/1','block2/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block2_Atot'],'Position',[380.0, 70.0, 400.0, 90.0]);
set_param([sys '/block2_Atot'],'Value','0.0');
set_param([sys '/block2_Atot'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block1/11','block2_Atot_adder/1','autorouting','on');
add_line(sys, 'block2_Atot/1','block2_Atot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block2_Btot'],'Position',[400.0, 130.0, 420.0, 150.0]);
set_param([sys '/block2_Btot'],'Value','1.0');
set_param([sys '/block2_Btot'],'BackgroundColor','[0.754,0.828,0.934]'); add_line(sys, 'block2_Btot/1','block2/2','autorouting','on');
add_line(sys, 'block2/11','block2/3','autorouting','on');

%Output

add_block('simulink/Commonly Used Blocks/Scope', [sys '/outputscope'],'Position',[810.0, 50, 840.0, 80]);
set_param([sys '/outputscope'],'NumInputPorts','4');

% Output block 'E + S <> ES (kf = 10.0, kr = 5.0)'
add_line(sys, 'block1/1','outputscope/1','autorouting','on');
add_line(sys, 'block1/3','outputscope/2','autorouting','on');

% Output block 'ES > P + E (kf = 1000.0)'
add_line(sys, 'block2/1','outputscope/3','autorouting','on');
add_line(sys, 'block2/11','outputscope/4','autorouting','on');

% Assign images
img = imread('chip-block.png');
% block1
set_param([sys '/block1'], 'UserDataPersistent', 'on');
ud.img = img;
set_param([sys '/block1'], 'UserData', ud);
Simulink.Mask.create([sys '/block1']);
set_param([sys '/block1'],'MaskDisplay','ud = get_param([''catalysis'' ''/E + S <> ES (kf = 10.0, kr = 5.0)''], ''UserData''); image(ud.img);');
set_param([sys '/block1'], 'MaskIconOpaque', 'opaque-with-ports');
% block2
set_param([sys '/block2'], 'UserDataPersistent', 'on');
ud.img = img;
set_param([sys '/block2'], 'UserData', ud);
Simulink.Mask.create([sys '/block2']);
set_param([sys '/block2'],'MaskDisplay','ud = get_param([''catalysis'' ''/ES > P + E (kf = 1000.0)''], ''UserData''); image(ud.img);');
set_param([sys '/block2'], 'MaskIconOpaque', 'opaque-with-ports');

% Rename blocks
set_param([sys '/block1'],'Name','E + S <> ES (kf = 10.0, kr = 5.0)');
set_param([sys '/block2'],'Name','ES > P + E (kf = 1000.0)');
