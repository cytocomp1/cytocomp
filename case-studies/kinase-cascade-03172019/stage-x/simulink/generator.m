sys = 'cascade';
new_system(sys);
open_system(sys);
% Block 'Block __s0 + __s1 <> __s6 (kf = 1.0E-5, kr = 0.1)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block1'],'Position',[70.0, 70.0, 310.0, 490.0]);
set_param([sys '/block1'],'KDfw','99999.99999999999');
set_param([sys '/block1'],'KDrv','10.0');
set_param([sys '/block1'],'kr','1.0');
set_param([sys '/block1'],'kdeg','1.0');
set_param([sys '/block1'],'ratC','0.0');
set_param([sys '/block1'],'n','1.0');
set_param([sys '/block1'],'A_FB_EN','1');
set_param([sys '/block1'],'B_FB_EN','1');
set_param([sys '/block1'],'sel_rate','1');
set_param([sys '/block1'],'sel_Ctot','1');

% Block 'Block __s6 > __s0 + __s7 (kf = 0.1) (for __s0)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block2'],'Position',[450.0, 70.0, 690.0, 490.0]);
set_param([sys '/block2'],'KDfw','10.0');
set_param([sys '/block2'],'KDrv','1.0');
set_param([sys '/block2'],'kr','1.0');
set_param([sys '/block2'],'kdeg','1.0');
set_param([sys '/block2'],'ratC','1.0');
set_param([sys '/block2'],'n','1.0');
set_param([sys '/block2'],'A_FB_EN','1');
set_param([sys '/block2'],'B_FB_EN','0');
set_param([sys '/block2'],'sel_rate','1');
set_param([sys '/block2'],'sel_Ctot','1');

% Block 'Block __s6 > __s0 + __s7 (kf = 0.1) (for __s7)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block3'],'Position',[830.0, 70.0, 1070.0, 490.0]);
set_param([sys '/block3'],'KDfw','10.0');
set_param([sys '/block3'],'KDrv','1.0');
set_param([sys '/block3'],'kr','1.0');
set_param([sys '/block3'],'kdeg','1.0');
set_param([sys '/block3'],'ratC','0.0');
set_param([sys '/block3'],'n','1.0');
set_param([sys '/block3'],'A_FB_EN','0');
set_param([sys '/block3'],'B_FB_EN','0');
set_param([sys '/block3'],'sel_rate','1');
set_param([sys '/block3'],'sel_Ctot','1');

% Block 'Block __s4 + __s7 <> __s8 (kf = 1.0E-5, kr = 0.1)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block4'],'Position',[1210.0, 70.0, 1450.0, 490.0]);
set_param([sys '/block4'],'KDfw','99999.99999999999');
set_param([sys '/block4'],'KDrv','10.0');
set_param([sys '/block4'],'kr','1.0');
set_param([sys '/block4'],'kdeg','1.0');
set_param([sys '/block4'],'ratC','0.0');
set_param([sys '/block4'],'n','1.0');
set_param([sys '/block4'],'A_FB_EN','1');
set_param([sys '/block4'],'B_FB_EN','1');
set_param([sys '/block4'],'sel_rate','1');
set_param([sys '/block4'],'sel_Ctot','1');

% Block 'Block __s2 + __s7 <> __s9 (kf = 1.0E-5, kr = 0.1)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block5'],'Position',[1590.0, 70.0, 1830.0, 490.0]);
set_param([sys '/block5'],'KDfw','99999.99999999999');
set_param([sys '/block5'],'KDrv','10.0');
set_param([sys '/block5'],'kr','1.0');
set_param([sys '/block5'],'kdeg','1.0');
set_param([sys '/block5'],'ratC','0.0');
set_param([sys '/block5'],'n','1.0');
set_param([sys '/block5'],'A_FB_EN','1');
set_param([sys '/block5'],'B_FB_EN','0');
set_param([sys '/block5'],'sel_rate','1');
set_param([sys '/block5'],'sel_Ctot','1');

% Block 'Block __s8 > __s1 + __s4 (kf = 0.003) (for __s1)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block6'],'Position',[1970.0, 70.0, 2210.0, 490.0]);
set_param([sys '/block6'],'KDfw','333.3333333333333');
set_param([sys '/block6'],'KDrv','1.0');
set_param([sys '/block6'],'kr','1.0');
set_param([sys '/block6'],'kdeg','1.0');
set_param([sys '/block6'],'ratC','0.0');
set_param([sys '/block6'],'n','1.0');
set_param([sys '/block6'],'A_FB_EN','1');
set_param([sys '/block6'],'B_FB_EN','0');
set_param([sys '/block6'],'sel_rate','1');
set_param([sys '/block6'],'sel_Ctot','1');

% Block 'Block __s8 > __s1 + __s4 (kf = 0.003) (for __s4)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block7'],'Position',[2350.0, 70.0, 2590.0, 490.0]);
set_param([sys '/block7'],'KDfw','333.3333333333333');
set_param([sys '/block7'],'KDrv','1.0');
set_param([sys '/block7'],'kr','1.0');
set_param([sys '/block7'],'kdeg','1.0');
set_param([sys '/block7'],'ratC','0.0');
set_param([sys '/block7'],'n','1.0');
set_param([sys '/block7'],'A_FB_EN','0');
set_param([sys '/block7'],'B_FB_EN','0');
set_param([sys '/block7'],'sel_rate','1');
set_param([sys '/block7'],'sel_Ctot','1');

% Block 'Block __s9 > __s7 + __s10 (kf = 0.1) (for __s7)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block8'],'Position',[2730.0, 70.0, 2970.0, 490.0]);
set_param([sys '/block8'],'KDfw','10.0');
set_param([sys '/block8'],'KDrv','1.0');
set_param([sys '/block8'],'kr','1.0');
set_param([sys '/block8'],'kdeg','1.0');
set_param([sys '/block8'],'ratC','0.0');
set_param([sys '/block8'],'n','1.0');
set_param([sys '/block8'],'A_FB_EN','1');
set_param([sys '/block8'],'B_FB_EN','0');
set_param([sys '/block8'],'sel_rate','1');
set_param([sys '/block8'],'sel_Ctot','1');

% Block 'Block __s9 > __s7 + __s10 (kf = 0.1) (for __s10)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block9'],'Position',[70.0, 630.0, 310.0, 1050.0]);
set_param([sys '/block9'],'KDfw','10.0');
set_param([sys '/block9'],'KDrv','1.0');
set_param([sys '/block9'],'kr','1.0');
set_param([sys '/block9'],'kdeg','1.0');
set_param([sys '/block9'],'ratC','0.0');
set_param([sys '/block9'],'n','1.0');
set_param([sys '/block9'],'A_FB_EN','0');
set_param([sys '/block9'],'B_FB_EN','0');
set_param([sys '/block9'],'sel_rate','1');
set_param([sys '/block9'],'sel_Ctot','1');

% Block 'Block __s4 + __s10 <> __s11 (kf = 1.0E-5, kr = 0.1)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block10'],'Position',[450.0, 630.0, 690.0, 1050.0]);
set_param([sys '/block10'],'KDfw','99999.99999999999');
set_param([sys '/block10'],'KDrv','10.0');
set_param([sys '/block10'],'kr','1.0');
set_param([sys '/block10'],'kdeg','1.0');
set_param([sys '/block10'],'ratC','0.0');
set_param([sys '/block10'],'n','1.0');
set_param([sys '/block10'],'A_FB_EN','0');
set_param([sys '/block10'],'B_FB_EN','1');
set_param([sys '/block10'],'sel_rate','1');
set_param([sys '/block10'],'sel_Ctot','1');

% Block 'Block __s7 + __s10 <> __s12 (kf = 1.0E-5, kr = 0.1)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block11'],'Position',[830.0, 630.0, 1070.0, 1050.0]);
set_param([sys '/block11'],'KDfw','99999.99999999999');
set_param([sys '/block11'],'KDrv','10.0');
set_param([sys '/block11'],'kr','1.0');
set_param([sys '/block11'],'kdeg','1.0');
set_param([sys '/block11'],'ratC','0.0');
set_param([sys '/block11'],'n','1.0');
set_param([sys '/block11'],'A_FB_EN','0');
set_param([sys '/block11'],'B_FB_EN','0');
set_param([sys '/block11'],'sel_rate','1');
set_param([sys '/block11'],'sel_Ctot','1');

% Block 'Block __s11 > __s2 + __s4 (kf = 0.003) (for __s2)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block12'],'Position',[1210.0, 630.0, 1450.0, 1050.0]);
set_param([sys '/block12'],'KDfw','333.3333333333333');
set_param([sys '/block12'],'KDrv','1.0');
set_param([sys '/block12'],'kr','1.0');
set_param([sys '/block12'],'kdeg','1.0');
set_param([sys '/block12'],'ratC','0.0');
set_param([sys '/block12'],'n','1.0');
set_param([sys '/block12'],'A_FB_EN','1');
set_param([sys '/block12'],'B_FB_EN','0');
set_param([sys '/block12'],'sel_rate','1');
set_param([sys '/block12'],'sel_Ctot','1');

% Block 'Block __s11 > __s2 + __s4 (kf = 0.003) (for __s4)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block13'],'Position',[1590.0, 630.0, 1830.0, 1050.0]);
set_param([sys '/block13'],'KDfw','333.3333333333333');
set_param([sys '/block13'],'KDrv','1.0');
set_param([sys '/block13'],'kr','1.0');
set_param([sys '/block13'],'kdeg','1.0');
set_param([sys '/block13'],'ratC','0.0');
set_param([sys '/block13'],'n','1.0');
set_param([sys '/block13'],'A_FB_EN','0');
set_param([sys '/block13'],'B_FB_EN','0');
set_param([sys '/block13'],'sel_rate','1');
set_param([sys '/block13'],'sel_Ctot','1');

% Block 'Block __s12 > __s7 + __s13 (kf = 0.1) (for __s7)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block14'],'Position',[1970.0, 630.0, 2210.0, 1050.0]);
set_param([sys '/block14'],'KDfw','10.0');
set_param([sys '/block14'],'KDrv','1.0');
set_param([sys '/block14'],'kr','1.0');
set_param([sys '/block14'],'kdeg','1.0');
set_param([sys '/block14'],'ratC','0.0');
set_param([sys '/block14'],'n','1.0');
set_param([sys '/block14'],'A_FB_EN','1');
set_param([sys '/block14'],'B_FB_EN','0');
set_param([sys '/block14'],'sel_rate','1');
set_param([sys '/block14'],'sel_Ctot','1');

% Block 'Block __s12 > __s7 + __s13 (kf = 0.1) (for __s13)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block15'],'Position',[2350.0, 630.0, 2590.0, 1050.0]);
set_param([sys '/block15'],'KDfw','10.0');
set_param([sys '/block15'],'KDrv','1.0');
set_param([sys '/block15'],'kr','1.0');
set_param([sys '/block15'],'kdeg','1.0');
set_param([sys '/block15'],'ratC','0.0');
set_param([sys '/block15'],'n','1.0');
set_param([sys '/block15'],'A_FB_EN','0');
set_param([sys '/block15'],'B_FB_EN','0');
set_param([sys '/block15'],'sel_rate','1');
set_param([sys '/block15'],'sel_Ctot','1');

% Block 'Block __s4 + __s13 <> __s14 (kf = 1.0E-5, kr = 0.1)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block16'],'Position',[2730.0, 630.0, 2970.0, 1050.0]);
set_param([sys '/block16'],'KDfw','99999.99999999999');
set_param([sys '/block16'],'KDrv','10.0');
set_param([sys '/block16'],'kr','1.0');
set_param([sys '/block16'],'kdeg','1.0');
set_param([sys '/block16'],'ratC','0.0');
set_param([sys '/block16'],'n','1.0');
set_param([sys '/block16'],'A_FB_EN','0');
set_param([sys '/block16'],'B_FB_EN','1');
set_param([sys '/block16'],'sel_rate','1');
set_param([sys '/block16'],'sel_Ctot','1');

% Block 'Block __s3 + __s13 <> __s15 (kf = 1.0E-5, kr = 0.1)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block17'],'Position',[70.0, 1190.0, 310.0, 1610.0]);
set_param([sys '/block17'],'KDfw','99999.99999999999');
set_param([sys '/block17'],'KDrv','10.0');
set_param([sys '/block17'],'kr','1.0');
set_param([sys '/block17'],'kdeg','1.0');
set_param([sys '/block17'],'ratC','0.0');
set_param([sys '/block17'],'n','1.0');
set_param([sys '/block17'],'A_FB_EN','1');
set_param([sys '/block17'],'B_FB_EN','0');
set_param([sys '/block17'],'sel_rate','1');
set_param([sys '/block17'],'sel_Ctot','1');

% Block 'Block __s14 > __s4 + __s10 (kf = 0.003) (for __s4)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block18'],'Position',[450.0, 1190.0, 690.0, 1610.0]);
set_param([sys '/block18'],'KDfw','333.3333333333333');
set_param([sys '/block18'],'KDrv','1.0');
set_param([sys '/block18'],'kr','1.0');
set_param([sys '/block18'],'kdeg','1.0');
set_param([sys '/block18'],'ratC','0.0');
set_param([sys '/block18'],'n','1.0');
set_param([sys '/block18'],'A_FB_EN','1');
set_param([sys '/block18'],'B_FB_EN','0');
set_param([sys '/block18'],'sel_rate','1');
set_param([sys '/block18'],'sel_Ctot','1');

% Block 'Block __s14 > __s4 + __s10 (kf = 0.003) (for __s10)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block19'],'Position',[830.0, 1190.0, 1070.0, 1610.0]);
set_param([sys '/block19'],'KDfw','333.3333333333333');
set_param([sys '/block19'],'KDrv','1.0');
set_param([sys '/block19'],'kr','1.0');
set_param([sys '/block19'],'kdeg','1.0');
set_param([sys '/block19'],'ratC','0.0');
set_param([sys '/block19'],'n','1.0');
set_param([sys '/block19'],'A_FB_EN','0');
set_param([sys '/block19'],'B_FB_EN','0');
set_param([sys '/block19'],'sel_rate','1');
set_param([sys '/block19'],'sel_Ctot','1');

% Block 'Block __s15 > __s13 + __s16 (kf = 0.1) (for __s13)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block20'],'Position',[1210.0, 1190.0, 1450.0, 1610.0]);
set_param([sys '/block20'],'KDfw','10.0');
set_param([sys '/block20'],'KDrv','1.0');
set_param([sys '/block20'],'kr','1.0');
set_param([sys '/block20'],'kdeg','1.0');
set_param([sys '/block20'],'ratC','0.0');
set_param([sys '/block20'],'n','1.0');
set_param([sys '/block20'],'A_FB_EN','1');
set_param([sys '/block20'],'B_FB_EN','0');
set_param([sys '/block20'],'sel_rate','1');
set_param([sys '/block20'],'sel_Ctot','1');

% Block 'Block __s15 > __s13 + __s16 (kf = 0.1) (for __s16)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block21'],'Position',[1590.0, 1190.0, 1830.0, 1610.0]);
set_param([sys '/block21'],'KDfw','10.0');
set_param([sys '/block21'],'KDrv','1.0');
set_param([sys '/block21'],'kr','1.0');
set_param([sys '/block21'],'kdeg','1.0');
set_param([sys '/block21'],'ratC','0.0');
set_param([sys '/block21'],'n','1.0');
set_param([sys '/block21'],'A_FB_EN','0');
set_param([sys '/block21'],'B_FB_EN','0');
set_param([sys '/block21'],'sel_rate','1');
set_param([sys '/block21'],'sel_Ctot','1');

% Block 'Block __s5 + __s16 <> __s17 (kf = 1.0E-5, kr = 0.1)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block22'],'Position',[1970.0, 1190.0, 2210.0, 1610.0]);
set_param([sys '/block22'],'KDfw','99999.99999999999');
set_param([sys '/block22'],'KDrv','10.0');
set_param([sys '/block22'],'kr','1.0');
set_param([sys '/block22'],'kdeg','1.0');
set_param([sys '/block22'],'ratC','0.0');
set_param([sys '/block22'],'n','1.0');
set_param([sys '/block22'],'A_FB_EN','1');
set_param([sys '/block22'],'B_FB_EN','1');
set_param([sys '/block22'],'sel_rate','1');
set_param([sys '/block22'],'sel_Ctot','1');

% Block 'Block __s13 + __s16 <> __s18 (kf = 1.0E-5, kr = 0.1)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block23'],'Position',[2350.0, 1190.0, 2590.0, 1610.0]);
set_param([sys '/block23'],'KDfw','99999.99999999999');
set_param([sys '/block23'],'KDrv','10.0');
set_param([sys '/block23'],'kr','1.0');
set_param([sys '/block23'],'kdeg','1.0');
set_param([sys '/block23'],'ratC','0.0');
set_param([sys '/block23'],'n','1.0');
set_param([sys '/block23'],'A_FB_EN','0');
set_param([sys '/block23'],'B_FB_EN','0');
set_param([sys '/block23'],'sel_rate','1');
set_param([sys '/block23'],'sel_Ctot','1');

% Block 'Block __s17 > __s3 + __s5 (kf = 0.003) (for __s3)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block24'],'Position',[2730.0, 1190.0, 2970.0, 1610.0]);
set_param([sys '/block24'],'KDfw','333.3333333333333');
set_param([sys '/block24'],'KDrv','1.0');
set_param([sys '/block24'],'kr','1.0');
set_param([sys '/block24'],'kdeg','1.0');
set_param([sys '/block24'],'ratC','0.0');
set_param([sys '/block24'],'n','1.0');
set_param([sys '/block24'],'A_FB_EN','1');
set_param([sys '/block24'],'B_FB_EN','0');
set_param([sys '/block24'],'sel_rate','1');
set_param([sys '/block24'],'sel_Ctot','1');

% Block 'Block __s17 > __s3 + __s5 (kf = 0.003) (for __s5)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block25'],'Position',[70.0, 1750.0, 310.0, 2170.0]);
set_param([sys '/block25'],'KDfw','333.3333333333333');
set_param([sys '/block25'],'KDrv','1.0');
set_param([sys '/block25'],'kr','1.0');
set_param([sys '/block25'],'kdeg','1.0');
set_param([sys '/block25'],'ratC','0.0');
set_param([sys '/block25'],'n','1.0');
set_param([sys '/block25'],'A_FB_EN','0');
set_param([sys '/block25'],'B_FB_EN','0');
set_param([sys '/block25'],'sel_rate','1');
set_param([sys '/block25'],'sel_Ctot','1');

% Block 'Block __s18 > __s13 + __s19 (kf = 0.1) (for __s13)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block26'],'Position',[450.0, 1750.0, 690.0, 2170.0]);
set_param([sys '/block26'],'KDfw','10.0');
set_param([sys '/block26'],'KDrv','1.0');
set_param([sys '/block26'],'kr','1.0');
set_param([sys '/block26'],'kdeg','1.0');
set_param([sys '/block26'],'ratC','0.0');
set_param([sys '/block26'],'n','1.0');
set_param([sys '/block26'],'A_FB_EN','1');
set_param([sys '/block26'],'B_FB_EN','0');
set_param([sys '/block26'],'sel_rate','1');
set_param([sys '/block26'],'sel_Ctot','1');

% Block 'Block __s18 > __s13 + __s19 (kf = 0.1) (for __s19)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block27'],'Position',[830.0, 1750.0, 1070.0, 2170.0]);
set_param([sys '/block27'],'KDfw','10.0');
set_param([sys '/block27'],'KDrv','1.0');
set_param([sys '/block27'],'kr','1.0');
set_param([sys '/block27'],'kdeg','1.0');
set_param([sys '/block27'],'ratC','0.0');
set_param([sys '/block27'],'n','1.0');
set_param([sys '/block27'],'A_FB_EN','0');
set_param([sys '/block27'],'B_FB_EN','0');
set_param([sys '/block27'],'sel_rate','1');
set_param([sys '/block27'],'sel_Ctot','1');

% Block 'Block __s5 + __s19 <> __s20 (kf = 1.0E-5, kr = 0.1)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block28'],'Position',[1210.0, 1750.0, 1450.0, 2170.0]);
set_param([sys '/block28'],'KDfw','99999.99999999999');
set_param([sys '/block28'],'KDrv','10.0');
set_param([sys '/block28'],'kr','1.0');
set_param([sys '/block28'],'kdeg','1.0');
set_param([sys '/block28'],'ratC','0.0');
set_param([sys '/block28'],'n','1.0');
set_param([sys '/block28'],'A_FB_EN','0');
set_param([sys '/block28'],'B_FB_EN','1');
set_param([sys '/block28'],'sel_rate','1');
set_param([sys '/block28'],'sel_Ctot','1');

% Block 'Block __s20 > __s5 + __s16 (kf = 0.003) (for __s5)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block29'],'Position',[1590.0, 1750.0, 1830.0, 2170.0]);
set_param([sys '/block29'],'KDfw','333.3333333333333');
set_param([sys '/block29'],'KDrv','1.0');
set_param([sys '/block29'],'kr','1.0');
set_param([sys '/block29'],'kdeg','1.0');
set_param([sys '/block29'],'ratC','0.0');
set_param([sys '/block29'],'n','1.0');
set_param([sys '/block29'],'A_FB_EN','1');
set_param([sys '/block29'],'B_FB_EN','0');
set_param([sys '/block29'],'sel_rate','1');
set_param([sys '/block29'],'sel_Ctot','1');

% Block 'Block __s20 > __s5 + __s16 (kf = 0.003) (for __s16)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block30'],'Position',[1970.0, 1750.0, 2210.0, 2170.0]);
set_param([sys '/block30'],'KDfw','333.3333333333333');
set_param([sys '/block30'],'KDrv','1.0');
set_param([sys '/block30'],'kr','1.0');
set_param([sys '/block30'],'kdeg','1.0');
set_param([sys '/block30'],'ratC','0.0');
set_param([sys '/block30'],'n','1.0');
set_param([sys '/block30'],'A_FB_EN','0');
set_param([sys '/block30'],'B_FB_EN','0');
set_param([sys '/block30'],'sel_rate','1');
set_param([sys '/block30'],'sel_Ctot','1');


% ** Connections **

% Incoming connections for block '__s0 + __s1 <> __s6 (kf = 1.0E-5, kr = 0.1)'
add_block('simulink/Math Operations/Sum', [sys '/block1_Atot_adder'],'Position',[20.0, 70.0, 40.0, 90.0]);
set_param([sys '/block1_Atot_adder'],'Inputs','++');
add_line(sys, 'block1_Atot_adder/1','block1/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block1_Atot'],'Position',[0.0, 70.0, 20.0, 90.0]);
set_param([sys '/block1_Atot'],'Value','60000.0');
add_line(sys, 'block2/11','block1_Atot_adder/1','autorouting','on');
add_line(sys, 'block1_Atot/1','block1_Atot_adder/2','autorouting','on');
add_block('simulink/Math Operations/Sum', [sys '/block1_Btot_adder'],'Position',[20.0, 130.0, 40.0, 150.0]);
set_param([sys '/block1_Btot_adder'],'Inputs','++');
add_line(sys, 'block1_Btot_adder/1','block1/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block1_Btot'],'Position',[0.0, 130.0, 20.0, 150.0]);
set_param([sys '/block1_Btot'],'Value','70000.0');
add_line(sys, 'block6/11','block1_Btot_adder/1','autorouting','on');
add_line(sys, 'block1_Btot/1','block1_Btot_adder/2','autorouting','on');
add_line(sys, 'block2/1','block1/3','autorouting','on');
add_line(sys, 'block2/16','block1/6','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block1_Dfree'],'Position',[20.0, 250.0, 40.0, 270.0]);
set_param([sys '/block1_Dfree'],'Value','1.0');
add_line(sys, 'block1_Dfree/1','block1/4','autorouting','on');

% Incoming connections for block '__s6 > __s0 + __s7 (kf = 0.1) (for __s0)'
add_block('simulink/Math Operations/Sum', [sys '/block2_Atot_adder'],'Position',[400.0, 70.0, 420.0, 90.0]);
set_param([sys '/block2_Atot_adder'],'Inputs','++');
add_line(sys, 'block2_Atot_adder/1','block2/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block2_Atot'],'Position',[380.0, 70.0, 400.0, 90.0]);
set_param([sys '/block2_Atot'],'Value','0.0');
add_line(sys, 'block1/11','block2_Atot_adder/1','autorouting','on');
add_line(sys, 'block2_Atot/1','block2_Atot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block2_Btot'],'Position',[400.0, 130.0, 420.0, 150.0]);
set_param([sys '/block2_Btot'],'Value','1.0');
add_line(sys, 'block2_Btot/1','block2/2','autorouting','on');

% Incoming connections for block '__s6 > __s0 + __s7 (kf = 0.1) (for __s7)'
add_block('simulink/Sources/Constant', [sys '/block3_Btot'],'Position',[780.0, 130.0, 800.0, 150.0]);
set_param([sys '/block3_Btot'],'Value','1.0');
add_line(sys, 'block3_Btot/1','block3/2','autorouting','on');
add_block('simulink/Math Operations/Sum', [sys '/block3_Cprod_adder'],'Position',[780.0, 370.0, 800.0, 390.0]);
set_param([sys '/block3_Cprod_adder'],'Inputs','+++');
add_line(sys, 'block3_Cprod_adder/1','block3/6','autorouting','on');
add_line(sys, 'block2/5','block3_Cprod_adder/1','autorouting','on');
add_line(sys, 'block8/5','block3_Cprod_adder/2','autorouting','on');
add_line(sys, 'block14/5','block3_Cprod_adder/3','autorouting','on');
add_block('simulink/Math Operations/Sum', [sys '/block3_Cdeg_adder'],'Position',[780.0, 310.0, 800.0, 330.0]);
set_param([sys '/block3_Cdeg_adder'],'Inputs','+++');
add_line(sys, 'block3_Cdeg_adder/1','block3/5','autorouting','on');
add_line(sys, 'block2/14','block3_Cdeg_adder/1','autorouting','on');
add_line(sys, 'block8/14','block3_Cdeg_adder/2','autorouting','on');
add_line(sys, 'block14/14','block3_Cdeg_adder/3','autorouting','on');

% Incoming connections for block '__s4 + __s7 <> __s8 (kf = 1.0E-5, kr = 0.1)'
add_block('simulink/Math Operations/Sum', [sys '/block4_Atot_adder'],'Position',[1160.0, 70.0, 1180.0, 90.0]);
set_param([sys '/block4_Atot_adder'],'Inputs','++++');
add_line(sys, 'block4_Atot_adder/1','block4/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block4_Atot'],'Position',[1140.0, 70.0, 1160.0, 90.0]);
set_param([sys '/block4_Atot'],'Value','200000.0');
add_line(sys, 'block10/9','block4_Atot_adder/1','autorouting','on');
add_line(sys, 'block16/9','block4_Atot_adder/2','autorouting','on');
add_line(sys, 'block7/11','block4_Atot_adder/3','autorouting','on');
add_line(sys, 'block4_Atot/1','block4_Atot_adder/4','autorouting','on');
add_block('simulink/Math Operations/Sum', [sys '/block4_Btot_adder'],'Position',[1160.0, 130.0, 1180.0, 150.0]);
set_param([sys '/block4_Btot_adder'],'Inputs','++++');
add_line(sys, 'block4_Btot_adder/1','block4/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block4_Btot'],'Position',[1140.0, 130.0, 1160.0, 150.0]);
set_param([sys '/block4_Btot'],'Value','0.0');
add_line(sys, 'block5/9','block4_Btot_adder/1','autorouting','on');
add_line(sys, 'block11/9','block4_Btot_adder/2','autorouting','on');
add_line(sys, 'block3/11','block4_Btot_adder/3','autorouting','on');
add_line(sys, 'block4_Btot/1','block4_Btot_adder/4','autorouting','on');
add_line(sys, 'block6/1','block4/3','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block4_Dfree'],'Position',[1160.0, 250.0, 1180.0, 270.0]);
set_param([sys '/block4_Dfree'],'Value','1.0');
add_line(sys, 'block4_Dfree/1','block4/4','autorouting','on');

% Incoming connections for block '__s2 + __s7 <> __s9 (kf = 1.0E-5, kr = 0.1)'
add_block('simulink/Math Operations/Sum', [sys '/block5_Atot_adder'],'Position',[1540.0, 70.0, 1560.0, 90.0]);
set_param([sys '/block5_Atot_adder'],'Inputs','++');
add_line(sys, 'block5_Atot_adder/1','block5/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block5_Atot'],'Position',[1520.0, 70.0, 1540.0, 90.0]);
set_param([sys '/block5_Atot'],'Value','3000000.0');
add_line(sys, 'block12/11','block5_Atot_adder/1','autorouting','on');
add_line(sys, 'block5_Atot/1','block5_Atot_adder/2','autorouting','on');
add_line(sys, 'block4/3','block5/2','autorouting','on');
add_line(sys, 'block8/1','block5/3','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block5_Dfree'],'Position',[1540.0, 250.0, 1560.0, 270.0]);
set_param([sys '/block5_Dfree'],'Value','1.0');
add_line(sys, 'block5_Dfree/1','block5/4','autorouting','on');

% Incoming connections for block '__s8 > __s1 + __s4 (kf = 0.003) (for __s1)'
add_block('simulink/Math Operations/Sum', [sys '/block6_Atot_adder'],'Position',[1920.0, 70.0, 1940.0, 90.0]);
set_param([sys '/block6_Atot_adder'],'Inputs','++');
add_line(sys, 'block6_Atot_adder/1','block6/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block6_Atot'],'Position',[1900.0, 70.0, 1920.0, 90.0]);
set_param([sys '/block6_Atot'],'Value','0.0');
add_line(sys, 'block4/11','block6_Atot_adder/1','autorouting','on');
add_line(sys, 'block6_Atot/1','block6_Atot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block6_Btot'],'Position',[1920.0, 130.0, 1940.0, 150.0]);
set_param([sys '/block6_Btot'],'Value','1.0');
add_line(sys, 'block6_Btot/1','block6/2','autorouting','on');

% Incoming connections for block '__s8 > __s1 + __s4 (kf = 0.003) (for __s4)'
add_block('simulink/Sources/Constant', [sys '/block7_Btot'],'Position',[2300.0, 130.0, 2320.0, 150.0]);
set_param([sys '/block7_Btot'],'Value','1.0');
add_line(sys, 'block7_Btot/1','block7/2','autorouting','on');
add_block('simulink/Math Operations/Sum', [sys '/block7_Cprod_adder'],'Position',[2300.0, 370.0, 2320.0, 390.0]);
set_param([sys '/block7_Cprod_adder'],'Inputs','+++');
add_line(sys, 'block7_Cprod_adder/1','block7/6','autorouting','on');
add_line(sys, 'block6/5','block7_Cprod_adder/1','autorouting','on');
add_line(sys, 'block12/5','block7_Cprod_adder/2','autorouting','on');
add_line(sys, 'block18/5','block7_Cprod_adder/3','autorouting','on');
add_block('simulink/Math Operations/Sum', [sys '/block7_Cdeg_adder'],'Position',[2300.0, 310.0, 2320.0, 330.0]);
set_param([sys '/block7_Cdeg_adder'],'Inputs','+++');
add_line(sys, 'block7_Cdeg_adder/1','block7/5','autorouting','on');
add_line(sys, 'block6/14','block7_Cdeg_adder/1','autorouting','on');
add_line(sys, 'block12/14','block7_Cdeg_adder/2','autorouting','on');
add_line(sys, 'block18/14','block7_Cdeg_adder/3','autorouting','on');

% Incoming connections for block '__s9 > __s7 + __s10 (kf = 0.1) (for __s7)'
add_block('simulink/Math Operations/Sum', [sys '/block8_Atot_adder'],'Position',[2680.0, 70.0, 2700.0, 90.0]);
set_param([sys '/block8_Atot_adder'],'Inputs','++');
add_line(sys, 'block8_Atot_adder/1','block8/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block8_Atot'],'Position',[2660.0, 70.0, 2680.0, 90.0]);
set_param([sys '/block8_Atot'],'Value','0.0');
add_line(sys, 'block5/11','block8_Atot_adder/1','autorouting','on');
add_line(sys, 'block8_Atot/1','block8_Atot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block8_Btot'],'Position',[2680.0, 130.0, 2700.0, 150.0]);
set_param([sys '/block8_Btot'],'Value','1.0');
add_line(sys, 'block8_Btot/1','block8/2','autorouting','on');
add_line(sys, 'block8/11','block8/3','autorouting','on');

% Incoming connections for block '__s9 > __s7 + __s10 (kf = 0.1) (for __s10)'
add_block('simulink/Sources/Constant', [sys '/block9_Btot'],'Position',[20.0, 690.0, 40.0, 710.0]);
set_param([sys '/block9_Btot'],'Value','1.0');
add_line(sys, 'block9_Btot/1','block9/2','autorouting','on');
add_block('simulink/Math Operations/Sum', [sys '/block9_Cprod_adder'],'Position',[20.0, 930.0, 40.0, 950.0]);
set_param([sys '/block9_Cprod_adder'],'Inputs','++');
add_line(sys, 'block9_Cprod_adder/1','block9/6','autorouting','on');
add_line(sys, 'block8/5','block9_Cprod_adder/1','autorouting','on');
add_line(sys, 'block18/5','block9_Cprod_adder/2','autorouting','on');
add_block('simulink/Math Operations/Sum', [sys '/block9_Cdeg_adder'],'Position',[20.0, 870.0, 40.0, 890.0]);
set_param([sys '/block9_Cdeg_adder'],'Inputs','++');
add_line(sys, 'block9_Cdeg_adder/1','block9/5','autorouting','on');
add_line(sys, 'block8/14','block9_Cdeg_adder/1','autorouting','on');
add_line(sys, 'block18/14','block9_Cdeg_adder/2','autorouting','on');

% Incoming connections for block '__s4 + __s10 <> __s11 (kf = 1.0E-5, kr = 0.1)'
add_line(sys, 'block4/1','block10/1','autorouting','on');
add_block('simulink/Math Operations/Sum', [sys '/block10_Btot_adder'],'Position',[400.0, 690.0, 420.0, 710.0]);
set_param([sys '/block10_Btot_adder'],'Inputs','+++');
add_line(sys, 'block10_Btot_adder/1','block10/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block10_Btot'],'Position',[380.0, 690.0, 400.0, 710.0]);
set_param([sys '/block10_Btot'],'Value','0.0');
add_line(sys, 'block11/9','block10_Btot_adder/1','autorouting','on');
add_line(sys, 'block9/11','block10_Btot_adder/2','autorouting','on');
add_line(sys, 'block10_Btot/1','block10_Btot_adder/3','autorouting','on');
add_line(sys, 'block12/1','block10/3','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block10_Dfree'],'Position',[400.0, 810.0, 420.0, 830.0]);
set_param([sys '/block10_Dfree'],'Value','1.0');
add_line(sys, 'block10_Dfree/1','block10/4','autorouting','on');

% Incoming connections for block '__s7 + __s10 <> __s12 (kf = 1.0E-5, kr = 0.1)'
add_line(sys, 'block4/3','block11/1','autorouting','on');
add_line(sys, 'block10/3','block11/2','autorouting','on');
add_line(sys, 'block14/1','block11/3','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block11_Dfree'],'Position',[780.0, 810.0, 800.0, 830.0]);
set_param([sys '/block11_Dfree'],'Value','1.0');
add_line(sys, 'block11_Dfree/1','block11/4','autorouting','on');

% Incoming connections for block '__s11 > __s2 + __s4 (kf = 0.003) (for __s2)'
add_block('simulink/Math Operations/Sum', [sys '/block12_Atot_adder'],'Position',[1160.0, 630.0, 1180.0, 650.0]);
set_param([sys '/block12_Atot_adder'],'Inputs','++');
add_line(sys, 'block12_Atot_adder/1','block12/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block12_Atot'],'Position',[1140.0, 630.0, 1160.0, 650.0]);
set_param([sys '/block12_Atot'],'Value','0.0');
add_line(sys, 'block10/11','block12_Atot_adder/1','autorouting','on');
add_line(sys, 'block12_Atot/1','block12_Atot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block12_Btot'],'Position',[1160.0, 690.0, 1180.0, 710.0]);
set_param([sys '/block12_Btot'],'Value','1.0');
add_line(sys, 'block12_Btot/1','block12/2','autorouting','on');

% Incoming connections for block '__s11 > __s2 + __s4 (kf = 0.003) (for __s4)'
add_block('simulink/Sources/Constant', [sys '/block13_Btot'],'Position',[1540.0, 690.0, 1560.0, 710.0]);
set_param([sys '/block13_Btot'],'Value','1.0');
add_line(sys, 'block13_Btot/1','block13/2','autorouting','on');
add_line(sys, 'block13/11','block13/3','autorouting','on');
add_line(sys, 'block12/5','block13/6','autorouting','on');
add_line(sys, 'block12/14','block13/5','autorouting','on');

% Incoming connections for block '__s12 > __s7 + __s13 (kf = 0.1) (for __s7)'
add_block('simulink/Math Operations/Sum', [sys '/block14_Atot_adder'],'Position',[1920.0, 630.0, 1940.0, 650.0]);
set_param([sys '/block14_Atot_adder'],'Inputs','++');
add_line(sys, 'block14_Atot_adder/1','block14/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block14_Atot'],'Position',[1900.0, 630.0, 1920.0, 650.0]);
set_param([sys '/block14_Atot'],'Value','0.0');
add_line(sys, 'block11/11','block14_Atot_adder/1','autorouting','on');
add_line(sys, 'block14_Atot/1','block14_Atot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block14_Btot'],'Position',[1920.0, 690.0, 1940.0, 710.0]);
set_param([sys '/block14_Btot'],'Value','1.0');
add_line(sys, 'block14_Btot/1','block14/2','autorouting','on');
add_line(sys, 'block14/11','block14/3','autorouting','on');

% Incoming connections for block '__s12 > __s7 + __s13 (kf = 0.1) (for __s13)'
add_block('simulink/Sources/Constant', [sys '/block15_Btot'],'Position',[2300.0, 690.0, 2320.0, 710.0]);
set_param([sys '/block15_Btot'],'Value','1.0');
add_line(sys, 'block15_Btot/1','block15/2','autorouting','on');
add_block('simulink/Math Operations/Sum', [sys '/block15_Cprod_adder'],'Position',[2300.0, 930.0, 2320.0, 950.0]);
set_param([sys '/block15_Cprod_adder'],'Inputs','+++');
add_line(sys, 'block15_Cprod_adder/1','block15/6','autorouting','on');
add_line(sys, 'block14/5','block15_Cprod_adder/1','autorouting','on');
add_line(sys, 'block20/5','block15_Cprod_adder/2','autorouting','on');
add_line(sys, 'block26/5','block15_Cprod_adder/3','autorouting','on');
add_block('simulink/Math Operations/Sum', [sys '/block15_Cdeg_adder'],'Position',[2300.0, 870.0, 2320.0, 890.0]);
set_param([sys '/block15_Cdeg_adder'],'Inputs','+++');
add_line(sys, 'block15_Cdeg_adder/1','block15/5','autorouting','on');
add_line(sys, 'block14/14','block15_Cdeg_adder/1','autorouting','on');
add_line(sys, 'block20/14','block15_Cdeg_adder/2','autorouting','on');
add_line(sys, 'block26/14','block15_Cdeg_adder/3','autorouting','on');

% Incoming connections for block '__s4 + __s13 <> __s14 (kf = 1.0E-5, kr = 0.1)'
add_line(sys, 'block4/1','block16/1','autorouting','on');
add_block('simulink/Math Operations/Sum', [sys '/block16_Btot_adder'],'Position',[2680.0, 690.0, 2700.0, 710.0]);
set_param([sys '/block16_Btot_adder'],'Inputs','++++');
add_line(sys, 'block16_Btot_adder/1','block16/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block16_Btot'],'Position',[2660.0, 690.0, 2680.0, 710.0]);
set_param([sys '/block16_Btot'],'Value','0.0');
add_line(sys, 'block17/9','block16_Btot_adder/1','autorouting','on');
add_line(sys, 'block23/9','block16_Btot_adder/2','autorouting','on');
add_line(sys, 'block15/11','block16_Btot_adder/3','autorouting','on');
add_line(sys, 'block16_Btot/1','block16_Btot_adder/4','autorouting','on');
add_line(sys, 'block18/1','block16/3','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block16_Dfree'],'Position',[2680.0, 810.0, 2700.0, 830.0]);
set_param([sys '/block16_Dfree'],'Value','1.0');
add_line(sys, 'block16_Dfree/1','block16/4','autorouting','on');

% Incoming connections for block '__s3 + __s13 <> __s15 (kf = 1.0E-5, kr = 0.1)'
add_block('simulink/Math Operations/Sum', [sys '/block17_Atot_adder'],'Position',[20.0, 1190.0, 40.0, 1210.0]);
set_param([sys '/block17_Atot_adder'],'Inputs','++');
add_line(sys, 'block17_Atot_adder/1','block17/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block17_Atot'],'Position',[0.0, 1190.0, 20.0, 1210.0]);
set_param([sys '/block17_Atot'],'Value','700000.0');
add_line(sys, 'block24/11','block17_Atot_adder/1','autorouting','on');
add_line(sys, 'block17_Atot/1','block17_Atot_adder/2','autorouting','on');
add_line(sys, 'block16/3','block17/2','autorouting','on');
add_line(sys, 'block20/1','block17/3','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block17_Dfree'],'Position',[20.0, 1370.0, 40.0, 1390.0]);
set_param([sys '/block17_Dfree'],'Value','1.0');
add_line(sys, 'block17_Dfree/1','block17/4','autorouting','on');

% Incoming connections for block '__s14 > __s4 + __s10 (kf = 0.003) (for __s4)'
add_block('simulink/Math Operations/Sum', [sys '/block18_Atot_adder'],'Position',[400.0, 1190.0, 420.0, 1210.0]);
set_param([sys '/block18_Atot_adder'],'Inputs','++');
add_line(sys, 'block18_Atot_adder/1','block18/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block18_Atot'],'Position',[380.0, 1190.0, 400.0, 1210.0]);
set_param([sys '/block18_Atot'],'Value','0.0');
add_line(sys, 'block16/11','block18_Atot_adder/1','autorouting','on');
add_line(sys, 'block18_Atot/1','block18_Atot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block18_Btot'],'Position',[400.0, 1250.0, 420.0, 1270.0]);
set_param([sys '/block18_Btot'],'Value','1.0');
add_line(sys, 'block18_Btot/1','block18/2','autorouting','on');
add_line(sys, 'block18/11','block18/3','autorouting','on');

% Incoming connections for block '__s14 > __s4 + __s10 (kf = 0.003) (for __s10)'
add_block('simulink/Sources/Constant', [sys '/block19_Btot'],'Position',[780.0, 1250.0, 800.0, 1270.0]);
set_param([sys '/block19_Btot'],'Value','1.0');
add_line(sys, 'block19_Btot/1','block19/2','autorouting','on');
add_line(sys, 'block19/11','block19/3','autorouting','on');
add_line(sys, 'block18/5','block19/6','autorouting','on');
add_line(sys, 'block18/14','block19/5','autorouting','on');

% Incoming connections for block '__s15 > __s13 + __s16 (kf = 0.1) (for __s13)'
add_block('simulink/Math Operations/Sum', [sys '/block20_Atot_adder'],'Position',[1160.0, 1190.0, 1180.0, 1210.0]);
set_param([sys '/block20_Atot_adder'],'Inputs','++');
add_line(sys, 'block20_Atot_adder/1','block20/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block20_Atot'],'Position',[1140.0, 1190.0, 1160.0, 1210.0]);
set_param([sys '/block20_Atot'],'Value','0.0');
add_line(sys, 'block17/11','block20_Atot_adder/1','autorouting','on');
add_line(sys, 'block20_Atot/1','block20_Atot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block20_Btot'],'Position',[1160.0, 1250.0, 1180.0, 1270.0]);
set_param([sys '/block20_Btot'],'Value','1.0');
add_line(sys, 'block20_Btot/1','block20/2','autorouting','on');
add_line(sys, 'block20/11','block20/3','autorouting','on');

% Incoming connections for block '__s15 > __s13 + __s16 (kf = 0.1) (for __s16)'
add_block('simulink/Sources/Constant', [sys '/block21_Btot'],'Position',[1540.0, 1250.0, 1560.0, 1270.0]);
set_param([sys '/block21_Btot'],'Value','1.0');
add_line(sys, 'block21_Btot/1','block21/2','autorouting','on');
add_block('simulink/Math Operations/Sum', [sys '/block21_Cprod_adder'],'Position',[1540.0, 1490.0, 1560.0, 1510.0]);
set_param([sys '/block21_Cprod_adder'],'Inputs','++');
add_line(sys, 'block21_Cprod_adder/1','block21/6','autorouting','on');
add_line(sys, 'block20/5','block21_Cprod_adder/1','autorouting','on');
add_line(sys, 'block29/5','block21_Cprod_adder/2','autorouting','on');
add_block('simulink/Math Operations/Sum', [sys '/block21_Cdeg_adder'],'Position',[1540.0, 1430.0, 1560.0, 1450.0]);
set_param([sys '/block21_Cdeg_adder'],'Inputs','++');
add_line(sys, 'block21_Cdeg_adder/1','block21/5','autorouting','on');
add_line(sys, 'block20/14','block21_Cdeg_adder/1','autorouting','on');
add_line(sys, 'block29/14','block21_Cdeg_adder/2','autorouting','on');

% Incoming connections for block '__s5 + __s16 <> __s17 (kf = 1.0E-5, kr = 0.1)'
add_block('simulink/Math Operations/Sum', [sys '/block22_Atot_adder'],'Position',[1920.0, 1190.0, 1940.0, 1210.0]);
set_param([sys '/block22_Atot_adder'],'Inputs','+++');
add_line(sys, 'block22_Atot_adder/1','block22/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block22_Atot'],'Position',[1900.0, 1190.0, 1920.0, 1210.0]);
set_param([sys '/block22_Atot'],'Value','17000.0');
add_line(sys, 'block28/9','block22_Atot_adder/1','autorouting','on');
add_line(sys, 'block25/11','block22_Atot_adder/2','autorouting','on');
add_line(sys, 'block22_Atot/1','block22_Atot_adder/3','autorouting','on');
add_block('simulink/Math Operations/Sum', [sys '/block22_Btot_adder'],'Position',[1920.0, 1250.0, 1940.0, 1270.0]);
set_param([sys '/block22_Btot_adder'],'Inputs','+++');
add_line(sys, 'block22_Btot_adder/1','block22/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block22_Btot'],'Position',[1900.0, 1250.0, 1920.0, 1270.0]);
set_param([sys '/block22_Btot'],'Value','0.0');
add_line(sys, 'block23/9','block22_Btot_adder/1','autorouting','on');
add_line(sys, 'block21/11','block22_Btot_adder/2','autorouting','on');
add_line(sys, 'block22_Btot/1','block22_Btot_adder/3','autorouting','on');
add_line(sys, 'block24/1','block22/3','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block22_Dfree'],'Position',[1920.0, 1370.0, 1940.0, 1390.0]);
set_param([sys '/block22_Dfree'],'Value','1.0');
add_line(sys, 'block22_Dfree/1','block22/4','autorouting','on');

% Incoming connections for block '__s13 + __s16 <> __s18 (kf = 1.0E-5, kr = 0.1)'
add_line(sys, 'block16/3','block23/1','autorouting','on');
add_line(sys, 'block22/3','block23/2','autorouting','on');
add_line(sys, 'block26/1','block23/3','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block23_Dfree'],'Position',[2300.0, 1370.0, 2320.0, 1390.0]);
set_param([sys '/block23_Dfree'],'Value','1.0');
add_line(sys, 'block23_Dfree/1','block23/4','autorouting','on');

% Incoming connections for block '__s17 > __s3 + __s5 (kf = 0.003) (for __s3)'
add_block('simulink/Math Operations/Sum', [sys '/block24_Atot_adder'],'Position',[2680.0, 1190.0, 2700.0, 1210.0]);
set_param([sys '/block24_Atot_adder'],'Inputs','++');
add_line(sys, 'block24_Atot_adder/1','block24/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block24_Atot'],'Position',[2660.0, 1190.0, 2680.0, 1210.0]);
set_param([sys '/block24_Atot'],'Value','0.0');
add_line(sys, 'block22/11','block24_Atot_adder/1','autorouting','on');
add_line(sys, 'block24_Atot/1','block24_Atot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block24_Btot'],'Position',[2680.0, 1250.0, 2700.0, 1270.0]);
set_param([sys '/block24_Btot'],'Value','1.0');
add_line(sys, 'block24_Btot/1','block24/2','autorouting','on');

% Incoming connections for block '__s17 > __s3 + __s5 (kf = 0.003) (for __s5)'
add_block('simulink/Sources/Constant', [sys '/block25_Btot'],'Position',[20.0, 1810.0, 40.0, 1830.0]);
set_param([sys '/block25_Btot'],'Value','1.0');
add_line(sys, 'block25_Btot/1','block25/2','autorouting','on');
add_block('simulink/Math Operations/Sum', [sys '/block25_Cprod_adder'],'Position',[20.0, 2050.0, 40.0, 2070.0]);
set_param([sys '/block25_Cprod_adder'],'Inputs','++');
add_line(sys, 'block25_Cprod_adder/1','block25/6','autorouting','on');
add_line(sys, 'block24/5','block25_Cprod_adder/1','autorouting','on');
add_line(sys, 'block29/5','block25_Cprod_adder/2','autorouting','on');
add_block('simulink/Math Operations/Sum', [sys '/block25_Cdeg_adder'],'Position',[20.0, 1990.0, 40.0, 2010.0]);
set_param([sys '/block25_Cdeg_adder'],'Inputs','++');
add_line(sys, 'block25_Cdeg_adder/1','block25/5','autorouting','on');
add_line(sys, 'block24/14','block25_Cdeg_adder/1','autorouting','on');
add_line(sys, 'block29/14','block25_Cdeg_adder/2','autorouting','on');

% Incoming connections for block '__s18 > __s13 + __s19 (kf = 0.1) (for __s13)'
add_block('simulink/Math Operations/Sum', [sys '/block26_Atot_adder'],'Position',[400.0, 1750.0, 420.0, 1770.0]);
set_param([sys '/block26_Atot_adder'],'Inputs','++');
add_line(sys, 'block26_Atot_adder/1','block26/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block26_Atot'],'Position',[380.0, 1750.0, 400.0, 1770.0]);
set_param([sys '/block26_Atot'],'Value','0.0');
add_line(sys, 'block23/11','block26_Atot_adder/1','autorouting','on');
add_line(sys, 'block26_Atot/1','block26_Atot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block26_Btot'],'Position',[400.0, 1810.0, 420.0, 1830.0]);
set_param([sys '/block26_Btot'],'Value','1.0');
add_line(sys, 'block26_Btot/1','block26/2','autorouting','on');
add_line(sys, 'block26/11','block26/3','autorouting','on');

% Incoming connections for block '__s18 > __s13 + __s19 (kf = 0.1) (for __s19)'
add_block('simulink/Sources/Constant', [sys '/block27_Btot'],'Position',[780.0, 1810.0, 800.0, 1830.0]);
set_param([sys '/block27_Btot'],'Value','1.0');
add_line(sys, 'block27_Btot/1','block27/2','autorouting','on');
add_line(sys, 'block26/5','block27/6','autorouting','on');
add_line(sys, 'block26/14','block27/5','autorouting','on');

% Incoming connections for block '__s5 + __s19 <> __s20 (kf = 1.0E-5, kr = 0.1)'
add_line(sys, 'block22/1','block28/1','autorouting','on');
add_block('simulink/Math Operations/Sum', [sys '/block28_Btot_adder'],'Position',[1160.0, 1810.0, 1180.0, 1830.0]);
set_param([sys '/block28_Btot_adder'],'Inputs','++');
add_line(sys, 'block28_Btot_adder/1','block28/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block28_Btot'],'Position',[1140.0, 1810.0, 1160.0, 1830.0]);
set_param([sys '/block28_Btot'],'Value','0.0');
add_line(sys, 'block27/11','block28_Btot_adder/1','autorouting','on');
add_line(sys, 'block28_Btot/1','block28_Btot_adder/2','autorouting','on');
add_line(sys, 'block29/1','block28/3','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block28_Dfree'],'Position',[1160.0, 1930.0, 1180.0, 1950.0]);
set_param([sys '/block28_Dfree'],'Value','1.0');
add_line(sys, 'block28_Dfree/1','block28/4','autorouting','on');

% Incoming connections for block '__s20 > __s5 + __s16 (kf = 0.003) (for __s5)'
add_block('simulink/Math Operations/Sum', [sys '/block29_Atot_adder'],'Position',[1540.0, 1750.0, 1560.0, 1770.0]);
set_param([sys '/block29_Atot_adder'],'Inputs','++');
add_line(sys, 'block29_Atot_adder/1','block29/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block29_Atot'],'Position',[1520.0, 1750.0, 1540.0, 1770.0]);
set_param([sys '/block29_Atot'],'Value','0.0');
add_line(sys, 'block28/11','block29_Atot_adder/1','autorouting','on');
add_line(sys, 'block29_Atot/1','block29_Atot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block29_Btot'],'Position',[1540.0, 1810.0, 1560.0, 1830.0]);
set_param([sys '/block29_Btot'],'Value','1.0');
add_line(sys, 'block29_Btot/1','block29/2','autorouting','on');
add_line(sys, 'block29/11','block29/3','autorouting','on');

% Incoming connections for block '__s20 > __s5 + __s16 (kf = 0.003) (for __s16)'
add_block('simulink/Sources/Constant', [sys '/block30_Btot'],'Position',[1920.0, 1810.0, 1940.0, 1830.0]);
set_param([sys '/block30_Btot'],'Value','1.0');
add_line(sys, 'block30_Btot/1','block30/2','autorouting','on');
add_line(sys, 'block30/11','block30/3','autorouting','on');
add_line(sys, 'block29/5','block30/6','autorouting','on');
add_line(sys, 'block29/14','block30/5','autorouting','on');

%Output

add_block('simulink/Commonly Used Blocks/Scope', [sys '/outputscope'],'Position',[3090.0, 50, 3120.0, 80]);
set_param([sys '/outputscope'],'NumInputPorts','21');

% Output block '__s0 + __s1 <> __s6 (kf = 1.0E-5, kr = 0.1)'
add_line(sys, 'block1/1','outputscope/1','autorouting','on');
add_line(sys, 'block1/3','outputscope/2','autorouting','on');

% Output block '__s6 > __s0 + __s7 (kf = 0.1) (for __s0)'
add_line(sys, 'block2/1','outputscope/3','autorouting','on');

% Output block '__s6 > __s0 + __s7 (kf = 0.1) (for __s7)'

% Output block '__s4 + __s7 <> __s8 (kf = 1.0E-5, kr = 0.1)'
add_line(sys, 'block4/1','outputscope/4','autorouting','on');
add_line(sys, 'block4/3','outputscope/5','autorouting','on');

% Output block '__s2 + __s7 <> __s9 (kf = 1.0E-5, kr = 0.1)'
add_line(sys, 'block5/1','outputscope/6','autorouting','on');

% Output block '__s8 > __s1 + __s4 (kf = 0.003) (for __s1)'
add_line(sys, 'block6/1','outputscope/7','autorouting','on');

% Output block '__s8 > __s1 + __s4 (kf = 0.003) (for __s4)'

% Output block '__s9 > __s7 + __s10 (kf = 0.1) (for __s7)'
add_line(sys, 'block8/1','outputscope/8','autorouting','on');

% Output block '__s9 > __s7 + __s10 (kf = 0.1) (for __s10)'

% Output block '__s4 + __s10 <> __s11 (kf = 1.0E-5, kr = 0.1)'
add_line(sys, 'block10/3','outputscope/9','autorouting','on');

% Output block '__s7 + __s10 <> __s12 (kf = 1.0E-5, kr = 0.1)'

% Output block '__s11 > __s2 + __s4 (kf = 0.003) (for __s2)'
add_line(sys, 'block12/1','outputscope/10','autorouting','on');

% Output block '__s11 > __s2 + __s4 (kf = 0.003) (for __s4)'

% Output block '__s12 > __s7 + __s13 (kf = 0.1) (for __s7)'
add_line(sys, 'block14/1','outputscope/11','autorouting','on');

% Output block '__s12 > __s7 + __s13 (kf = 0.1) (for __s13)'

% Output block '__s4 + __s13 <> __s14 (kf = 1.0E-5, kr = 0.1)'
add_line(sys, 'block16/3','outputscope/12','autorouting','on');

% Output block '__s3 + __s13 <> __s15 (kf = 1.0E-5, kr = 0.1)'
add_line(sys, 'block17/1','outputscope/13','autorouting','on');

% Output block '__s14 > __s4 + __s10 (kf = 0.003) (for __s4)'
add_line(sys, 'block18/1','outputscope/14','autorouting','on');

% Output block '__s14 > __s4 + __s10 (kf = 0.003) (for __s10)'

% Output block '__s15 > __s13 + __s16 (kf = 0.1) (for __s13)'
add_line(sys, 'block20/1','outputscope/15','autorouting','on');

% Output block '__s15 > __s13 + __s16 (kf = 0.1) (for __s16)'

% Output block '__s5 + __s16 <> __s17 (kf = 1.0E-5, kr = 0.1)'
add_line(sys, 'block22/1','outputscope/16','autorouting','on');
add_line(sys, 'block22/3','outputscope/17','autorouting','on');

% Output block '__s13 + __s16 <> __s18 (kf = 1.0E-5, kr = 0.1)'

% Output block '__s17 > __s3 + __s5 (kf = 0.003) (for __s3)'
add_line(sys, 'block24/1','outputscope/18','autorouting','on');

% Output block '__s17 > __s3 + __s5 (kf = 0.003) (for __s5)'

% Output block '__s18 > __s13 + __s19 (kf = 0.1) (for __s13)'
add_line(sys, 'block26/1','outputscope/19','autorouting','on');

% Output block '__s18 > __s13 + __s19 (kf = 0.1) (for __s19)'

% Output block '__s5 + __s19 <> __s20 (kf = 1.0E-5, kr = 0.1)'
add_line(sys, 'block28/3','outputscope/20','autorouting','on');

% Output block '__s20 > __s5 + __s16 (kf = 0.003) (for __s5)'
add_line(sys, 'block29/1','outputscope/21','autorouting','on');

% Output block '__s20 > __s5 + __s16 (kf = 0.003) (for __s16)'
