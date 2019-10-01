sys = 'cascade';
new_system(sys);
open_system(sys);
% Block 'Block Block 1'
add_block('Chip_Library/Aug15_Jon1',[sys '/block1'],'Position',[70.0, 70.0, 310.0, 490.0]);
set_param([sys '/block1'],'KDfw','50.0');
set_param([sys '/block1'],'KDrv','1000.0');
set_param([sys '/block1'],'kr','10.0');
set_param([sys '/block1'],'kdeg','1.0');
set_param([sys '/block1'],'ratC','10.0');
set_param([sys '/block1'],'n','1.0');
set_param([sys '/block1'],'A_FB_EN','0');
set_param([sys '/block1'],'B_FB_EN','0');
set_param([sys '/block1'],'sel_rate','1');
set_param([sys '/block1'],'sel_Ctot','1');

% Block 'Block Block 2'
add_block('Chip_Library/Aug15_Jon1',[sys '/block2'],'Position',[450.0, 70.0, 690.0, 490.0]);
set_param([sys '/block2'],'KDfw','50.0');
set_param([sys '/block2'],'KDrv','1000.0');
set_param([sys '/block2'],'kr','1.0');
set_param([sys '/block2'],'kdeg','1.0');
set_param([sys '/block2'],'ratC','0.0');
set_param([sys '/block2'],'n','1.0');
set_param([sys '/block2'],'A_FB_EN','0');
set_param([sys '/block2'],'B_FB_EN','1');
set_param([sys '/block2'],'sel_rate','1');
set_param([sys '/block2'],'sel_Ctot','1');

% Block 'Block Block 3'
add_block('Chip_Library/Aug15_Jon1',[sys '/block3'],'Position',[830.0, 70.0, 1070.0, 490.0]);
set_param([sys '/block3'],'KDfw','50.0');
set_param([sys '/block3'],'KDrv','1000.0');
set_param([sys '/block3'],'kr','1.0');
set_param([sys '/block3'],'kdeg','1.0');
set_param([sys '/block3'],'ratC','10.0');
set_param([sys '/block3'],'n','1.0');
set_param([sys '/block3'],'A_FB_EN','0');
set_param([sys '/block3'],'B_FB_EN','1');
set_param([sys '/block3'],'sel_rate','1');
set_param([sys '/block3'],'sel_Ctot','1');


% ** Connections **

% Incoming connections for block 'Block 1'
add_line(sys, 'block1/11','block1/3','autorouting','on');
add_line(sys, 'block2/16','block1/5','autorouting','on');

% Incoming connections for block 'Block 2'
add_line(sys, 'block1/11','block2/2','autorouting','on');
add_line(sys, 'block3/16','block2/5','autorouting','on');

% Incoming connections for block 'Block 3'
add_line(sys, 'block2/11','block3/2','autorouting','on');
add_line(sys, 'block3/11','block3/3','autorouting','on');

%Output

add_block('simulink/Commonly Used Blocks/Scope', [sys '/outputscope'],'Position',[1190.0, 50, 1220.0, 80]);
set_param([sys '/outputscope'],'NumInputPorts','0');

% Output block 'Block 1'

% Output block 'Block 2'

% Output block 'Block 3'
