function prog_vect=Jon_Bigboard_Repressilator(prog_vect,FPGA_lib,FPGA_xptr)

% 150p, 100n, 150p, 400n, 150p, 1u, 150p
% IStotSweep(1,:) = digital_current_to_closest_bits('B00',100,1);
% 
% numPoints=length(IStotSweep(:,1));
% initSamples = 23;
% numSamples=500;
% Imeas=zeros(initSamples+numPoints*numSamples,7); %%%%%%%%%%%%%%%%%%%%%%% note!

%% ADC
% Scale factor from ADC output to analog current (when clk=5MHz)
ADC_scale=calibrated('ADC','Aug15',28); %5Mhz

% program ADC scale factors in the FPGA
scalefactors = [40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40];
chip_addr = 0;
DNAChip_program_chip_FPGA_initparam_aug15(FPGA_lib,FPGA_xptr,scalefactors,chip_addr);

% figure(1); clf; figure(2); clf;
% options={'o-','x-','+-','*-'};

%% set SRAM connection
%DNAChip_reset_sram_aug15_bigboard_repressilator_prtnchip(FPGA_lib,FPGA_xptr);
tstart=tic; % setting the start point for elapsed time measurement

in_all=repressilator_sram();

disp('Programming connection SRAM...')
pause(1);

%program_FPGA_sram_bigboard(FPGA_lib,FPGA_xptr,sram_data_prtn,sram_data_gene)
sram_data_prtn=in_all;
for i=0:1
    chip_addr = i;
    program_FPGA(sram_data_prtn,chip_addr,FPGA_lib,FPGA_xptr,'prtn','sram');  
    %program_FPGA_trigger(FPGA_lib,FPGA_xptr);
    %DNAChip_program_chip_FPGA_sram_aug15(FPGA_lib,FPGA_xptr,in_all,chip_addr);
end

disp('Programming chips using FPGA complete')

elapsedtime=toc(tstart); % read elapsed time
disp(['SRAM programming took ' num2str(elapsedtime) ' seconds to run.'])

% set simulation values
% recommended clk freq: 5MHz
disp('Loading simulation parameters..')
pause(1);

%% Initial Conditions
prog_vect=repressilator_sr(prog_vect);
for i=0:9
    program_FPGA(prog_vect,i,FPGA_lib,FPGA_xptr,'prtn','sr');
end

disp('Initial condition set.')
pause(5);

%% reset FIFO for test
% Run this before adding new prog_vects
FIFO_reset(FPGA_xptr);

numRead = 10;%Jon edited to current value %40 % 20=1.92s. 200=19.2s
numOutputVar1 = 12; numOutputVar2 = 12;
%readarray = DNAChip_read_chip_FPGA_aug15(FPGA_lib,FPGA_xptr,numRead,numOutputVar1,numOutputVar2,ADCOUTtoCurrent_chip3,ADCOUTtoCurrent_chip2);
raw = read_chip_FPGA(FPGA_lib,FPGA_xptr,numRead,numOutputVar1,numOutputVar2);
for i=[1:12]
    readarray(:,i)=polyval(ADC_scale(:,i),raw(:,i));
end
assignin('base','readarray',readarray);
assignin('base','raw',raw);

%% Plot
%figure(12);
close all

figure
hold on
time=[1:size(readarray)]*0.002;
for i= [1,2,3]
    plot(time,readarray(:,i),'linewidth',1.5)
end
xlabel('Time (s)');
ylabel('Proteins per cell (nA)');
legend('Protein1','Protein2','Protein3','Location','Northwest')

figure
hold on
time=[1:size(readarray)]*0.002;
for i= [4,5,6]
    plot(time,readarray(:,i),'linewidth',1.5)
end
xlabel('Time (s)');
ylabel('mRNA per cell (nA)');
legend('mRNA1','mRNA2','mRNA3','Location','Northwest')

end

function in_all=repressilator_sram()
%%%%%%%%% Change connection here %%%%%%%%%
% Group Selection: among group 0~3
% Block Selection: among block 0~4
% Variable selection: among variables 0~27, 31 (see below)
% 0-IAtot, 1-IBtot, 2-ICfree, 3-IDfree, 4-ICprod, 5-ICdeg, 6-ICtot_in,
% 7-ICtot_copyN1, 8-ICtot_copyN2, 9-ICtot_copyP1, 10-ICtot_copyP2, 11-ICtot_copyP3,
% 12-IAfree_copy1, 13-IAfree_copy2, 14-IBfree_copy1, 15-IBfree_copy2,
% 16-Irate_fw, 17-Irate_rv, 18-Irate_fw_tot, 19-Irate_rv_tot, 20-Irate_fw_up1, 21-Irate_fw_up2, 22-Irate_rv_up1, 23-Irate_rv_up2,
% 24-ICfree_copy, 25-IDfree_copy, 26-IOne, 27-rate_toNoise, 31-ADC_in/Itest1/Itest2
% Wire Selection: among SRAM wire 0~99
% ADC switch open: wireSel = 76~99 (ADC_IN<0~23>)

in_all = ones(1024,100);

%%%%% Protein 1
% IAfree_copy1 -> IAtot
in_all = connect_SRAM(in_all,0,0,12,0); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,0,1,0,0); % groupSel, blockSel, variableSel, wireSel

% ICtot_copyP1 -> ICfree
in_all = connect_SRAM(in_all,0,0,9,1); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,0,0,2,1); % groupSel, blockSel, variableSel, wireSel

% ICtot_copyP1 -> IAtot
in_all = connect_SRAM(in_all,0,1,9,2); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,0,2,0,2); % groupSel, blockSel, variableSel, wireSel

% ICtot_copyP2 -> ICfree
in_all = connect_SRAM(in_all,0,1,10,3); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,0,1,2,3); % groupSel, blockSel, variableSel, wireSel

% ICtot_copyP1 -> IBtot
in_all = connect_SRAM(in_all,0,2,9,4); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,1,0,1,4); % groupSel, blockSel, variableSel, wireSel

% ICtot_copyP2 -> ICfree
in_all = connect_SRAM(in_all,0,2,10,5); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,0,2,2,5); % groupSel, blockSel, variableSel, wireSel

%%%%% Protein 2
% IAfree -> IAtot
in_all = connect_SRAM(in_all,1,0,12,6); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,1,1,0,6); % groupSel, blockSel, variableSel, wireSel

% ICtot_copyP1 -> ICfree
in_all = connect_SRAM(in_all,1,0,9,7); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,1,0,2,7); % groupSel, blockSel, variableSel, wireSel

% ICtot_copyP1 -> IAtot
in_all = connect_SRAM(in_all,1,1,9,8); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,1,2,0,8); % groupSel, blockSel, variableSel, wireSel

% ICtot_copyP2 -> ICfree
in_all = connect_SRAM(in_all,1,1,10,9); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,1,1,2,9); % groupSel, blockSel, variableSel, wireSel

% ICtot_copyP1 -> IBtot
in_all = connect_SRAM(in_all,1,2,9,10); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,2,0,1,10); % groupSel, blockSel, variableSel, wireSel

% ICtot_copyP2 -> ICfree
in_all = connect_SRAM(in_all,1,2,10,11); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,1,2,2,11); % groupSel, blockSel, variableSel, wireSel

%%%%% Protein 3
% IAfree -> IAtot
in_all = connect_SRAM(in_all,2,0,12,12); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,2,1,0,12); % groupSel, blockSel, variableSel, wireSel

% ICtot_copyP1 -> ICfree
in_all = connect_SRAM(in_all,2,0,9,13); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,2,0,2,13); % groupSel, blockSel, variableSel, wireSel

% ICtot_copyP1 -> IAtot
in_all = connect_SRAM(in_all,2,1,9,14); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,2,2,0,14); % groupSel, blockSel, variableSel, wireSel

% ICtot_copyP2 -> ICfree
in_all = connect_SRAM(in_all,2,1,10,15); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,2,1,2,15); % groupSel, blockSel, variableSel, wireSel

% ICtot_copyP1 -> IBtot
in_all = connect_SRAM(in_all,2,2,9,16); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,0,0,1,16); % groupSel, blockSel, variableSel, wireSel

% ICtot_copyP2 -> ICfree
in_all = connect_SRAM(in_all,2,2,10,17); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,2,2,2,17); % groupSel, blockSel, variableSel, wireSel


connect_ADC=[88 89 90 91 92 93 94 95 96 97 98 99];
%connect_FPGAport=[13 14 15 16 17 18 19 20 21 22 23 24];
%connect_ind=[1 2 3 4 5 6 7 8 9 10 11 12];
% connect ICtot_copyP3 to SRAM wire 76
in_all = connect_SRAM(in_all,0,2,11,connect_ADC(1)); % groupSel, blockSel, variableSel, wireSel
% connect ICtot_copyP3 to SRAM wire 77
in_all = connect_SRAM(in_all,1,2,11,connect_ADC(2)); % groupSel, blockSel, variableSel, wireSel
% connect ICtot_copyP3 to SRAM wire 78
in_all = connect_SRAM(in_all,2,2,11,connect_ADC(3)); % groupSel, blockSel, variableSel, wireSel
% connect IAfree_copy2 to SRAM wire 79
in_all = connect_SRAM(in_all,0,1,11,connect_ADC(4)); % groupSel, blockSel, variableSel, wireSel
% connect ICtot_copyP3 to SRAM wire 80
in_all = connect_SRAM(in_all,1,1,11,connect_ADC(5)); % groupSel, blockSel, variableSel, wireSel
% connect ICtot_copyP3 to SRAM wire 81
in_all = connect_SRAM(in_all,2,1,11,connect_ADC(6)); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,2,1,18,connect_ADC(7)); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,2,1,19,connect_ADC(8)); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,2,1,12,connect_ADC(9)); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,2,1,7,connect_ADC(10)); % groupSel, blockSel, variableSel, wireSel
in_all = connect_SRAM(in_all,2,1,25,connect_ADC(11)); % groupSel, blockSel, variableSel, wireSel


% use ADC 0~5
for i=76:99
in_all = connect_SRAM(in_all,0,0,31,i); % groupSel, blockSel, variableSel, wireSel
end
% % connect IAtot to SRAM wire 0
% in_all = connect_SRAM(in_all,0,0,0,1); % groupSel, blockSel, variableSel, wireSel
% 
% % connect SRAM wire 0 to Itest1
% in_all = connect_SRAM(in_all,1,0,31,1); % groupSel, blockSel, variableSel, wireSel
% 
% % connect SRAM wire 1 to Itest2
% in_all = connect_SRAM(in_all,3,0,31,0); % groupSel, blockSel, variableSel, wireSel

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
end

function prog_vect=repressilator_sr(prog_vect)
for k = 1:4
    % Block 1
    prog_vect(1:7, k)=digital_current_to_closest_bits('IAtot_1',865,1); % IAtot = 865n
    prog_vect(8:14, k)=[0 0 0 0 0 0 0]; % IBtot = 0
    prog_vect(15:21, k)=digital_current_to_closest_bits('IKDfw_1',4,1); % IKDfw = 4n
    prog_vect(22:28, k)=digital_current_to_closest_bits('Ikr1_1',1000,1); % Ikr1 = 10n
    prog_vect(29:35, k)=digital_current_to_closest_bits('Ikr2_1',1,1); % Ikr2 = 1n
    prog_vect(36:42, k)=digital_current_to_closest_bits('IDfree_1',10,1); % IDfree = 10n
    prog_vect(43:49, k)=digital_current_to_closest_bits('IKDrv_1',10,1); % IKDrv = 10n
    prog_vect(50:56, k)=[0 0 0 0 0 0 0]; % IratC = 0
    
    % Block 2
    prog_vect(57:63, k)=digital_current_to_closest_bits('IAtot_2',0.865,1); % IAtot = 865p
    prog_vect(64:70, k)=digital_current_to_closest_bits('IBtot_2',10,1); % IBtot = 10n
    prog_vect(71:77, k)=digital_current_to_closest_bits('IKDfw_2',10,1); % IKDfw = 10n
    prog_vect(78:84, k)=digital_current_to_closest_bits('Ikr1_2',100,1); % Ikr1 = 10n
    prog_vect(85:91, k)=digital_current_to_closest_bits('Ikr2_2',1,1); % Ikr2 = 1n
    prog_vect(92:98, k)=digital_current_to_closest_bits('IDfree_2',10,1); % IDfree = 10n
    prog_vect(99:105, k)=digital_current_to_closest_bits('IKDrv_2',10,1); % IKDrv = 10n
    prog_vect(106:112, k)=[0 0 0 0 0 0 0]; % IratC = 0
    
    % Block 3
    prog_vect(113:119, k)=[0 0 0 0 0 0 0]; % IAtot = 0
    prog_vect(120:126, k)=digital_current_to_closest_bits('IBtot_3',10,1); % IBtot = 10n
    prog_vect(127:133, k)=digital_current_to_closest_bits('IKDfw_3',10,1); % IKDfw = 10n
    prog_vect(134:140, k)=digital_current_to_closest_bits('Ikr1_3',20,1); % Ikr1 = 20n
    prog_vect(141:147, k)=digital_current_to_closest_bits('Ikr2_3',1,1); % Ikr2 = 1n
    prog_vect(148:154, k)=digital_current_to_closest_bits('IDfree_3',10,1); % IDfree = 10n
    prog_vect(155:161, k)=digital_current_to_closest_bits('IKDrv_3',10,1); % IKDrv = 10n
    prog_vect(162:168, k)=[0 0 0 0 0 0 0]; % IratC = 0
    
    % Block 1
    prog_vect(288:291, k)=[0 1 0 1]; % hill_b<0:3> (1111: hill 1 ~ 0000: hill 4)
    prog_vect(292:293, k)=[1 0]; % A_FB_EN, B_FB_EN
    prog_vect(294:296, k)=[0 1 1]; % sel_useI, sel_Ctot, sel_rate
    % Block 2
    prog_vect(297:300, k)=[1 1 1 1]; % hill_b<0:3> (1111: hill 1 ~ 0000: hill 4)
    prog_vect(301:302, k)=[0 0]; % A_FB_EN, B_FB_EN
    prog_vect(303:305, k)=[0 1 1]; % sel_useI, sel_Ctot, sel_rate
    % Block 3
    prog_vect(306:309, k)=[1 1 1 1]; % hill_b<0:3> (1111: hill 1 ~ 0000: hill 4)
    prog_vect(310:311, k)=[0 0]; % A_FB_EN, B_FB_EN
    prog_vect(312:314, k)=[0 1 1]; % sel_useI, sel_Ctot, sel_rate
end
end
